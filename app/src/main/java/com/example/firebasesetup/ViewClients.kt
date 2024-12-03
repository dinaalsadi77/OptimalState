package com.example.firebasesetup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.RadioButton
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
import java.text.SimpleDateFormat
import java.util.Locale

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
            val intent = Intent(this, ProviderHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    private fun fetchClientsData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the table first (if refreshing)
                clientTable.removeAllViews()
                addHeaderRow()

                // Check if snapshot is empty or not
                if (!snapshot.exists()) {
                    Toast.makeText(this@ViewClients, "No data found", Toast.LENGTH_SHORT).show()
                    return
                }

                // Iterate through each user
                for (userSnapshot in snapshot.children) {
                    val role = userSnapshot.child("role").getValue(String::class.java) ?: "unknown"

                    // Filter: Only process users with role "client"
                    if (role != "Client") {
                        continue
                    }

                    val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: "N/A"
                    val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: "N/A"

                    // Get all status entries
                    val statusSnapshots = userSnapshot.child("status").children

                    var latestStatus: String = "none"
                    var latestTime: String = "N/A"
                    var latestTimestamp: Long = Long.MIN_VALUE

                    // Loop through status entries and find the most recent one based on time
                    for (statusSnapshot in statusSnapshots) {
                        val status = statusSnapshot.child("status").getValue(String::class.java) ?: "none"
                        val statusTime = statusSnapshot.child("time").getValue(String::class.java) ?: "N/A"

                        try {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val statusDate = dateFormat.parse(statusTime)

                            if (statusDate != null && statusDate.time > latestTimestamp) {
                                latestStatus = status
                                latestTime = statusTime
                                latestTimestamp = statusDate.time
                            }
                        } catch (e: Exception) {
                            Log.e("ViewClients", "Error parsing time: $statusTime", e)
                        }
                    }

                    val (datePart, timePart) = latestTime.split(" ", limit = 2).let { parts ->
                        if (parts.size == 2) {
                            Pair(parts[0], parts[1])
                        } else {
                            Pair("N/A", "N/A")
                        }
                    }


                    addClientRow("$firstName $lastName", datePart, timePart, latestStatus)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewClients, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addHeaderRow() {
        val headerRow = TableRow(this).apply {
            setBackgroundColor(Color.parseColor("#F1E0B8"))
        }

        val headers = arrayOf("Client", "Status", "Last Update", "Time")
        for (header in headers) {
            val headerText = TextView(this).apply {
                text = header
                setPadding(16, 16, 16, 16)
                gravity = Gravity.CENTER
            }
            headerRow.addView(headerText)
        }
        clientTable.addView(headerRow)
    }

    private fun addClientRow(name: String, date: String, time: String, status: String) {
        val row = TableRow(this)

        val nameView = TextView(this).apply {
            text = name
            setPadding(8, 8, 8, 8)
            gravity = Gravity.START
        }

        val statusView = TextView(this).apply {
            text = status
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
        }

        val dateView = TextView(this).apply {
            text = date
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
        }

        val timeView = TextView(this).apply {
            text = time
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
        }

        row.addView(nameView)
        row.addView(statusView)
        row.addView(dateView)
        row.addView(timeView)

        clientTable.addView(row)
    }
}



