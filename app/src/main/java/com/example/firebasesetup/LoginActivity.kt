package com.example.firebasesetup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var forgotPasswordTextView: TextView

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHomePage(currentUser.uid)
        }
    }

    private fun navigateToHomePage(uid: String) {
        val userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        userDatabaseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userRole = task.result?.child("role")?.value.toString()
                if (userRole == "Provider") {
                    // Redirect to Provider home page
                    val intent = Intent(this, ProviderHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (userRole == "Client") {
                    val intent = Intent(this, ClientHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("LoginActivity", "Unknown user role: $userRole")
                    Toast.makeText(this, "Unknown user role", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("LoginActivity", "Failed to fetch user data: ${task.exception?.message}")
                Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val editTextEmail = findViewById<EditText>(R.id.usernameEditText)
        val editTextPassword = findViewById<EditText>(R.id.passwordEditText)
        val buttonLog = findViewById<Button>(R.id.loginButton)
        val registerTextView = findViewById<TextView>(R.id.newUserTextView)
        val loginMessage = findViewById<TextView>(R.id.loginMessage)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        forgotPasswordTextView.setOnClickListener {
            showForgotPasswordDialog()
        }

        buttonLog.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            // Now navigate to home page
                            navigateToHomePage(currentUser.uid)
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        loginMessage.text = "Invalid credentials. Please try again."
                        loginMessage.visibility = View.VISIBLE
                    }
                }
        }
    }

    private fun showForgotPasswordDialog() {
        val emailEditText = EditText(this)
        emailEditText.hint = "Enter your email"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Forgot Password?")
            .setMessage("Enter your email address to receive a password reset link.")
            .setView(emailEditText)
            .setPositiveButton("Send Reset Link") { _, _ ->
                val email = emailEditText.text.toString().trim()
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                sendPasswordResetEmail(email)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error sending reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
