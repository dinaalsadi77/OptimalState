package com.example.firebasesetup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RemoveClientActivity : AppCompatActivity() {
    private lateinit var clientTable: TableLayout
    private lateinit var backButton: Button
    private lateinit var confirmButton: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_client)

        clientTable = findViewById(R.id.clientTable)
        backButton = findViewById(R.id.Back_btn)
        confirmButton = findViewById(R.id.Confirm_btn)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        populateClientTable()

        backButton.setOnClickListener {
            val intent = Intent(this, ProviderHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        confirmButton.setOnClickListener {
            deleteSelectedClients()
        }
    }

    private fun populateClientTable() {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clientTable.removeAllViews()

                // Add Header Row (this will not be processed by the loop later)
                val headerRow = TableRow(this@RemoveClientActivity)
                val nameHeader = TextView(this@RemoveClientActivity).apply {
                    text = "Name"
                    setPadding(8, 8, 8, 8)
                    gravity = Gravity.CENTER
                }
                val removeHeader = TextView(this@RemoveClientActivity).apply {
                    text = "Remove"
                    setPadding(8, 8, 8, 8)
                    gravity = Gravity.CENTER
                }
                headerRow.addView(nameHeader)
                headerRow.addView(removeHeader)
                clientTable.addView(headerRow)

                // Loop through the users from Firebase
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    val clientName = userSnapshot.child("email").getValue(String::class.java)
                    val userRole = userSnapshot.child("role").getValue(String::class.java)

                    // Create a new row for each client
                    if (userRole == "Client") {
                    val tableRow = TableRow(this@RemoveClientActivity)

                    val clientNameText = TextView(this@RemoveClientActivity).apply {
                        text = clientName
                        setPadding(8, 8, 8, 8)
                        gravity = Gravity.CENTER
                    }

                    val radioButton = RadioButton(this@RemoveClientActivity).apply {
                        setPadding(8, 8, 8, 8)
                        setTag(userId)
                    }

                    tableRow.addView(clientNameText)
                    tableRow.addView(radioButton)

                    clientTable.addView(tableRow)
                }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RemoveClientActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteSelectedClients() {
        for (i in 1 until clientTable.childCount) {
            val tableRow = clientTable.getChildAt(i) as TableRow
            val radioButton = tableRow.getChildAt(1) as RadioButton

            if (radioButton.isChecked) {
                val userId = radioButton.tag as? String

                if (userId != null) {
                    usersRef.child(userId).removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Client removed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to remove client", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}