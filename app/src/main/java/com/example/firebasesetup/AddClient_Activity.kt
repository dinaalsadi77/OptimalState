package com.example.firebasesetup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddClient_Activity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_client)

        usernameEditText = findViewById(R.id.usernameEditText)
        val back = findViewById<Button>(R.id.Back_btn)
        val add = findViewById<Button>(R.id.Add_btn)
        auth = FirebaseAuth.getInstance()

        add.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            if (isValidEmail(email)) {
                addClientToDatabase(email)
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }
        }

        back.setOnClickListener {
            val intent = Intent(this, ProviderHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun addClientToDatabase(email: String) {
        val database = FirebaseDatabase.getInstance()
        val clientRef = database.getReference("users") // Store users under the "users" node

        val client = hashMapOf(
            "email" to email,
            "role" to "client"
        )

        clientRef.push().setValue(client).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Client added successfully", Toast.LENGTH_SHORT).show()
                usernameEditText.text.clear()
            } else {
                Toast.makeText(this, "Error adding client", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
