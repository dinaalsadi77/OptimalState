package com.example.firebasesetup

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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
    private val statusColorMap = mapOf(
        "gold" to Pair(0xFFFFD700.toInt(), "Gold"),
        "red" to Pair(Color.RED, "Red"),
        "blue" to Pair(Color.BLUE, "Blue"),
        "white" to Pair(Color.WHITE, "White")
    )
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
                clientTable.removeAllViews()
                addHeaderRow()

                if (!snapshot.exists()) {
                    Toast.makeText(this@ViewClients, "No data found", Toast.LENGTH_SHORT).show()
                    return
                }

                for (userSnapshot in snapshot.children) {
                    val role = userSnapshot.child("role").getValue(String::class.java) ?: "unknown"

                    if (role != "Client") continue

                    val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: "N/A"
                    val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: "N/A"

                    val statusSnapshots = userSnapshot.child("status").children
                    var latestStatus: String = "none"
                    var latestTime: String = "N/A"
                    var latestTimestamp: Long = Long.MIN_VALUE

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
                        if (parts.size == 2) Pair(parts[0], parts[1]) else Pair("N/A", "N/A")
                    }

                    addClientRow("$firstName $lastName", datePart, timePart, latestStatus, userSnapshot.key ?: "")
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
                setPadding(8, 8, 8, 8)
                gravity = Gravity.CENTER
            }
            headerRow.addView(headerText)
        }
        clientTable.addView(headerRow)
    }

    private fun addClientRow(name: String, date: String, time: String, status: String, userId: String) {
        val row = TableRow(this)

        val nameView = TextView(this).apply {
            text = name
            setPadding(8, 8, 8, 8)
            gravity = Gravity.START
        }

        val statusLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(8, 8, 8, 8)
        }

        val (color) = getColorForStatus(status)
        val border = GradientDrawable().apply {
            setColor(color)
            setStroke(4, Color.BLACK)
        }

        val colorBox = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(60, 60).apply {
                setPadding(0, 8, 0, 8)
                gravity = Gravity.CENTER

            }
            background = border
        }

        statusLayout.addView(colorBox)

//        val statusTextView = TextView(this).apply {
//            text = "($colorName)"
//            setPadding(4, 4, 4, 4)
//            gravity = Gravity.CENTER
//        }
//        statusLayout.addView(statusTextView)

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
        row.addView(statusLayout)
        row.addView(dateView)
        row.addView(timeView)

        row.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java).apply {
                putExtra("userId", userId)
            }
            startActivity(intent)
        }

        clientTable.addView(row)
    }

    private fun getColorForStatus(status: String): Pair<Int, String> {
        val statusLower = status.lowercase()
        return statusColorMap[statusLower] ?: Pair(Color.GRAY, "None")
    }
}
