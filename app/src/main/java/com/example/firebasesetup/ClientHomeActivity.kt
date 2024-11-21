package com.example.firebasesetup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ClientHomeActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_home)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        val name = user.displayName ?: user.email ?: "Guest"

        val logout = findViewById<Button>(R.id.logoutbtn)
        val exercises = findViewById<Button>(R.id.viewCurrentExercisesBtn)
        val welcomeText = findViewById<TextView>(R.id.welcome)
        val history = findViewById<Button>(R.id.viewHistoryBtn)
        val settings = findViewById<Button>(R.id.settingsBtn)

        // Set the welcome text dynamically
        welcomeText.text = "Welcome $name"

        // Logout functionality
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        exercises.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }

        history.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        settings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            retrieveName(currentUser.uid)
        }
    }

    private fun retrieveName(uid: String) {
        val userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        userDatabaseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firstname = task.result?.child("firstName")?.value.toString()
                if (firstname.isNotEmpty()) {
                    // Use the first name for something if needed
                    // For example, updating the welcome message
                    val welcomeText = findViewById<TextView>(R.id.welcome)
                    welcomeText.text = "Welcome $firstname"
                }
            }
        }
    }
}
