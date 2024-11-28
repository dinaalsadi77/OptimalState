package com.example.firebasesetup

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssesmentActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_assesment2)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "No user is logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = currentUser.uid

        database = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("status")

        val blueFeelings = findViewById<TextView>(R.id.blueFeelings)
        val redFeelings = findViewById<TextView>(R.id.redFeelings)
        val goldFeelings = findViewById<TextView>(R.id.goldFeelings)
        val whiteFeelings = findViewById<TextView>(R.id.whiteFeelings)
        val backButton = findViewById<TextView>(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, ClientHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        blueFeelings.setOnClickListener {
            storeClickInFirebase("blue")
        }
        redFeelings.setOnClickListener {
            storeClickInFirebase("red")
        }
        goldFeelings.setOnClickListener {
            storeClickInFirebase("gold")
        }
        whiteFeelings.setOnClickListener {
            storeClickInFirebase("white")
        }
    }

    private fun storeClickInFirebase(status: String) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val clickData = mapOf(
            "status" to status,
            "time" to currentTime
        )

        database.push().setValue(clickData)
            .addOnSuccessListener {
                Toast.makeText(this, "$status feeling stored successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to store feeling: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
