package com.example.firebasesetup

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ChangeUserSettings_Activity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var dobInput: TextView
    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_settings)

        nameInput = findViewById(R.id.name_input)
        dobInput = findViewById(R.id.dob_input)
        currentPasswordInput = findViewById(R.id.current_password_input)
        newPasswordInput = findViewById(R.id.new_password_input)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        findViewById<Button>(R.id.save_btn).setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                saveProfileChanges(currentUser.uid)
            }
        }

        findViewById<Button>(R.id.delete_account_button).setOnClickListener {
            deleteAccount()
        }

        dobInput.setOnClickListener {
            showDatePickerDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            retrieveData(currentUser.uid)
        }
    }

    private fun retrieveData(uid: String) {
        val userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        userDatabaseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firstname = task.result?.child("firstName")?.value.toString()
                val DOB = task.result?.child("dob")?.value.toString()

                if (firstname.isNotEmpty() && DOB.isNotEmpty()) {
                    nameInput.setText(firstname)
                    dobInput.text = DOB
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveProfileChanges(uid: String) {
        val userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        val fullName = nameInput.text.toString().trim()
        val dob = dobInput.text.toString().trim()

        if (fullName.isNotEmpty() && dob.isNotEmpty()) {
            val nameParts = fullName.split(" ", limit = 2)
            val firstName = nameParts.getOrNull(0) ?: ""
            val lastName = nameParts.getOrNull(1) ?: ""

            if (firstName.isEmpty()) {
                Toast.makeText(this, "Please enter a valid first name.", Toast.LENGTH_SHORT).show()
                return
            }

            val updatedData = mapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "dob" to dob
            )

            userDatabaseRef.updateChildren(updatedData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }

        if (currentPasswordInput.text.toString().trim().isNotEmpty() && newPasswordInput.text.toString().trim().isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPasswordInput.text.toString())

            user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    user.updatePassword(newPasswordInput.text.toString()).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()

                            val newCredential = EmailAuthProvider.getCredential(user.email ?: "", newPasswordInput.text.toString())
                            user.reauthenticate(newCredential).addOnCompleteListener { reauthNewTask ->
                                if (reauthNewTask.isSuccessful) {
                                    Toast.makeText(this, "Re-authentication successful. You can now log in with the new password.", Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Re-authentication failed. Please log out and try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        val currentPassword = currentPasswordInput.text.toString().trim()

        if (user != null && currentPassword.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)

            user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    val userId = user.uid

                    val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                    databaseRef.removeValue().addOnCompleteListener { databaseDeleteTask ->
                        if (databaseDeleteTask.isSuccessful) {
                            user.delete().addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to delete account from Authentication", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Failed to delete user data from database", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Reauthentication failed. Please check your password.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // If the current password is empty or user is null, prompt user
            Toast.makeText(this, "Please enter your current password to delete account", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dobInput.text = formattedDate
        }, year, month, day)

        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }
}