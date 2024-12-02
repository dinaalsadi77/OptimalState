package com.example.firebasesetup

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class RemoveClientActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_client)

        // Initialize the database reference
        database = FirebaseDatabase.getInstance().reference

        // Fetch and display users in a table
        fetchAndDisplayUsers()
    }

    private fun fetchAndDisplayUsers() {
        val usersRef = database.child("users")
        val tableLayout: TableLayout = findViewById(R.id.statusTable)

        // Clear any existing rows except the header row
        tableLayout.removeViews(1, tableLayout.childCount - 1)

        // Listen for user data
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(
                        this@RemoveClientActivity,
                        "No users found in the database.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                // Iterate over each user
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue
                    val userFirstName = userSnapshot.child("FirstName").getValue(String::class.java)
                    val userLastName =  userSnapshot.child("LastName").getValue(String::class.java)

                    if (userFirstName != null && userLastName !=null) {
                        // Create a new table row
                        val tableRow = TableRow(this@RemoveClientActivity)
                        tableRow.layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )

                        // Add name text view to the row
                        val nameTextView = TextView(this@RemoveClientActivity)
                        nameTextView.text = "$userFirstName $userLastName"
                        nameTextView.setPadding(16, 16, 16, 16)
                        nameTextView.gravity = Gravity.CENTER

                        // Add remove button to the row
                        val removeButton = Button(this@RemoveClientActivity)
                        removeButton.text = "Remove"
                        removeButton.setPadding(16, 16, 16, 16)
                        removeButton.setOnClickListener {
                            removeUser(userId, tableRow, tableLayout)
                        }

                        // Add views to the row
                        tableRow.addView(nameTextView)
                        tableRow.addView(removeButton)

                        // Add the row to the table
                        tableLayout.addView(tableRow)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RemoveClientActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("RemoveClientActivity", "Firebase error: ${error.message}")
            }
        })
    }

    private fun removeUser(userId: String, tableRow: TableRow, tableLayout: TableLayout) {
        // Reference to the user in the database
        val userRef = database.child("users").child(userId)

        // Remove the user from Firebase
        userRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Remove the row from the table
                tableLayout.removeView(tableRow)
                Toast.makeText(this, "User removed successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to remove user.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e("RemoveClientActivity", "Error removing user: ${exception.message}")
        }
    }
}
