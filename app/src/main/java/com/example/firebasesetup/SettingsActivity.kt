package com.example.firebasesetup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()

        val back = findViewById<Button>(R.id.Back_btn)
        val shareWith_btn = findViewById<Button>(R.id.ShareWith_btn)

        back.setOnClickListener {
            navigateToHomePage()
        }

        shareWith_btn.setOnClickListener {
            shareApp()
        }
    }

    private fun navigateToHomePage() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)
            userDatabaseRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userRole = task.result?.child("role")?.value.toString()
                    if (userRole == "Provider") {
                        val intent = Intent(this, ProviderHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (userRole == "Client") {
                        val intent = Intent(this, ClientHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
        }
    }

    private fun shareApp() {
        val appLink = "https://drive.google.com/file/d/1Eea9aKb8UWjRsrdQ7bkDw6GFUutXbLpp/view?usp=sharing"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this amazing app: $appLink")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}
