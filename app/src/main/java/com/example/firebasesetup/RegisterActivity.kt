package com.example.firebasesetup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dobEditText: EditText

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        // Bind the views to their respective IDs
        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        dobEditText = findViewById<EditText>(R.id.dobEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val providerRadioGroup = findViewById<RadioGroup>(R.id.providerRadioGroup)
        val registerButton = findViewById<Button>(R.id.registerButton)

        dobEditText.setOnClickListener {
            showDatePickerDialog()
        }

        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val dob = dobEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check for empty fields
            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(this, "Enter last name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(dob)) {
                Toast.makeText(this, "Enter date of birth", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if any radio button is selected
            val selectedProviderId = providerRadioGroup.checkedRadioButtonId
            val selectedProviderButton: RadioButton = findViewById(selectedProviderId)

            // If no provider selected, show error
            if (selectedProviderId == -1) {
                Toast.makeText(this, "Select provider option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userRole = if (selectedProviderButton.id == R.id.providerYes) {
                "Provider" // User selected "Yes"
            } else {
                "Client" // User selected "No"
            }

            // After successfully creating the user, don't sign out immediately
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userDatabaseRef =
                            FirebaseDatabase.getInstance().getReference("users").child(userId!!)

                        // Save user data to the database
                        val user = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "dob" to dob,
                            "email" to email,
                            "role" to userRole
                        )

                        // Save user data after successful registration
                        userDatabaseRef.setValue(user)
                            .addOnSuccessListener {
                                // Show success message only if data is saved successfully
                                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                FirebaseAuth.getInstance().signOut()
                                // Navigate to the LoginActivity
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                // Handle any error that occurs while saving the data
                                Toast.makeText(this, "Error saving user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Set today's date as the maximum date
        val maxDate = calendar.timeInMillis // This gets the current date in milliseconds

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dobEditText.setText(formattedDate)
            }, year, month, day)

        // Set the maximum date to today's date
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }
}
