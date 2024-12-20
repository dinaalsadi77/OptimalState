package com.example.firebasesetup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject

class AddClient_Activity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var auth: FirebaseAuth
    private val DEFAULT_ROLE = "Client"
    private lateinit var database: FirebaseDatabase


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_client)

        usernameEditText = findViewById(R.id.usernameEditText)
        val back = findViewById<Button>(R.id.Back_btn)
        val add = findViewById<Button>(R.id.Add_btn)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        add.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            if (isValidEmail(email)) {
                addClientToDatabase(email, add, progressBar)
            } else {
                showToast("Please enter a valid email.")
                usernameEditText.text.clear()
            }
        }

        back.setOnClickListener {
            val intent = Intent(this, ProviderHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun addClientToDatabase(email: String, addButton: Button, progressBar: ProgressBar) {
        disableUI(addButton, progressBar)

        // Check if the user already exists
        val usersRef = database.getReference("users")
        usersRef.orderByChild("email").equalTo(email).get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    showToast("This user already exists.")
                    usernameEditText.text.clear()
                    enableUI(addButton, progressBar)
                } else {
                    createUser(email, addButton, progressBar)
                }
            }
            .addOnFailureListener { exception ->
                showToast("Error checking user existence: ${exception.localizedMessage}")
                usernameEditText.text.clear()
                enableUI(addButton, progressBar)
            }
    }

    private fun createUser(email: String, addButton: Button, progressBar: ProgressBar) {
        val url = "http://192.168.100.70:3000/createUser"
        val jsonRequest = JSONObject()
        val password = generateRandomPassword()
        jsonRequest.put("email", email)
        jsonRequest.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonRequest,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        val userId = response.getString("userId")
                        storeClientRoleInDatabase(userId, email, addButton, progressBar)
                    } else {
                        val errorMsg = response.optString("error", "Unknown error occurred")
                        showToast("Failed to add client: $errorMsg")
                        usernameEditText.text.clear()
                        enableUI(addButton, progressBar)
                    }
                } catch (e: Exception) {
                    showToast("Error parsing response: ${e.message}")
                    usernameEditText.text.clear()
                    enableUI(addButton, progressBar)
                }
            },
            { error ->
                showToast("Error: ${error.message}")
                usernameEditText.text.clear()
                enableUI(addButton, progressBar)
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun storeClientRoleInDatabase(userId: String, email: String, addButton: Button, progressBar: ProgressBar) {
        val usersRef = database.getReference("users/$userId")

        val userDetails = mapOf(
            "email" to email,
            "role" to DEFAULT_ROLE,
            "firstName" to "User"
        )

        usersRef.setValue(userDetails)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendPasswordResetEmail(email, addButton, progressBar)
                } else {
                    showToast("Failed to store client role in database: ${task.exception?.localizedMessage}")
                    enableUI(addButton, progressBar)
                }
            }
    }

    private fun sendPasswordResetEmail(email: String, addButton: Button, progressBar: ProgressBar) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Client added successfully. A password reset email has been sent to $email.")
                } else {
                    showToast("Failed to send password reset email: ${task.exception?.localizedMessage}")
                }
                usernameEditText.text.clear()
                enableUI(addButton, progressBar)
            }
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?"
        return (1..12)
            .map { chars.random() }
            .joinToString("")
    }

    private fun isValidEmail(email: String): Boolean {
        return email.length in 5..50 && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun disableUI(button: Button, progressBar: ProgressBar) {
        button.isEnabled = false
        progressBar.visibility = View.VISIBLE
    }

    private fun enableUI(button: Button, progressBar: ProgressBar) {
        button.isEnabled = true
        progressBar.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}