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

class ProviderHomeActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_provider_home)

        val logout = findViewById<Button>(R.id.logoutbtn)
        val settings= findViewById<Button>(R.id.settingsBtn)
        val addClient=findViewById<Button>(R.id.addClients_btn)
        val removeClient=findViewById<Button>(R.id.removeClients)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!


        addClient.setOnClickListener{
            val intent = Intent(this, AddClient_Activity::class.java)
            startActivity(intent)
            finish()
        }
        removeClient.setOnClickListener{
            val intent=Intent(this,RemoveClientActivity::class.java)
            startActivity(intent)
            finish()
        }
        settings.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
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
                    val welcomeText = findViewById<TextView>(R.id.welcome)
                    welcomeText.text = "Welcome $firstname"
                }
            }
        }
    }
}
