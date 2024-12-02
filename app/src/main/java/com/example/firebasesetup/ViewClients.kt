package com.example.firebasesetup

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewClients : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var clientTable: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_clients)

        clientTable = findViewById(R.id.clientTable)
        val backButton: Button = findViewById(R.id.backButton)

        // Firebase database reference
        database = FirebaseDatabase.getInstance().reference.child("users")

        fetchClientsData()


        backButton.setOnClickListener {
            finish()
        }

    }
    private fun fetchClientsData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the table first (if refreshing)
                clientTable.removeAllViews()

                // Iterate through each user
                for (userSnapshot in snapshot.children) {
                    val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: "N/A"
                    val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: "N/A"
                    val dob = userSnapshot.child("dob").getValue(String::class.java) ?: "N/A"
                    val role = userSnapshot.child("role").getValue(String::class.java) ?: "N/A"

                    // Status data (optional)
                    val statusSnapshot = userSnapshot.child("status")
                    val statusId = statusSnapshot.child("id").getValue(String::class.java) ?: "N/A"
                    val statusText = statusSnapshot.child("status").getValue(String::class.java) ?: "none"
                    val time = statusSnapshot.child("time").getValue(String::class.java) ?: "N/A"

                    // Add a row to the table
                    addClientRow("$firstName $lastName", dob, time, statusText)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewClients, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addClientRow(name: String, date: String, time: String, status: String) {
        val row = TableRow(this)

        val nameView = TextView(this).apply {
            text = name
            setPadding(8, 8, 8, 8)
        }

        val dateView = TextView(this).apply {
            text = date
            setPadding(8, 8, 8, 8)
        }

        val timeView = TextView(this).apply {
            text = time
            setPadding(8, 8, 8, 8)
        }

        val statusView = View(this).apply {
            val color = when (status.lowercase()) {
                "red" -> Color.RED
                "yellow" -> Color.YELLOW
                else -> Color.TRANSPARENT
            }
            setBackgroundColor(color)
            layoutParams = TableRow.LayoutParams(50, 50)
        }

        row.addView(nameView)
        row.addView(dateView)
        row.addView(timeView)
        row.addView(statusView)

        clientTable.addView(row)
    }
}