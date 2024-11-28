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
        val notifications_btn = findViewById<Button>(R.id.SetCheckinNotifications_btn)
        val changeSettings_btn = findViewById<Button>(R.id.ChangeUserSettings_btn)

        changeSettings_btn.setOnClickListener {
            val intent = Intent(this, ChangeUserSettings_Activity::class.java)
            startActivity(intent)
            finish()
        }

        notifications_btn.setOnClickListener {
            val intent = Intent(this, notificationsActivity::class.java)
            startActivity(intent)
            finish()
        }

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
