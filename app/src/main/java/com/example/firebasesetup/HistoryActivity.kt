package com.example.firebasesetup

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar

class HistoryActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val daySpinner: Spinner = findViewById(R.id.daySpinner)
        val monthSpinner: Spinner = findViewById(R.id.monthSpinner)
        val yearSpinner: Spinner = findViewById(R.id.yearSpinner)
        val fetchButton: Button = findViewById(R.id.fetchButton)
        val resultTextView: TextView = findViewById(R.id.resultTextView)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "No user is logged in. Please log in first.", Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        val selectedUserId = intent.getStringExtra("userId")
        val userId = selectedUserId ?: currentUser.uid


        val calendar = Calendar.getInstance()
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayYear = calendar.get(Calendar.YEAR)

        // Day Spinner
        val days = (1..31).map { it.toString() }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = dayAdapter
        daySpinner.setSelection(todayDay - 1)

        // Month Spinner
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter
        monthSpinner.setSelection(todayMonth)

        // Year Spinner
        val years = (1900..2050).map { it.toString() }
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        val yearIndex = years.indexOf(todayYear.toString())
        if (yearIndex >= 0) {
            yearSpinner.setSelection(yearIndex)
        }

        fetchButton.setOnClickListener {
            if (daySpinner.selectedItem == null || monthSpinner.selectedItem == null || yearSpinner.selectedItem == null) {
                Toast.makeText(this, "Please select a valid date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedDay = daySpinner.selectedItem.toString().padStart(2, '0')
            val selectedMonth = (monthSpinner.selectedItemPosition + 1).toString().padStart(2, '0')
            val selectedYear = yearSpinner.selectedItem.toString()
            val selectedDate = "$selectedYear-$selectedMonth-$selectedDay"

            resultTextView.text = "Fetching data..."
            fetchData(userId, selectedDate, resultTextView)
        }
    }

    private val statusColorMap = mapOf(
        "gold" to Pair(0xFFFFD700.toInt(), "Gold"),
        "red" to Pair(android.graphics.Color.RED, "Red"),
        "blue" to Pair(android.graphics.Color.BLUE, "Blue"),
        "white" to Pair(android.graphics.Color.WHITE, "White")
    )

    private fun fetchData(userId: String, date: String, resultTextView: TextView) {
        val statusRef = database.child("users").child(userId).child("status")
        val tableLayout: TableLayout = findViewById(R.id.statusTable)

        // Clear existing rows except the header
        tableLayout.removeViews(1, tableLayout.childCount - 1)

        statusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val statusesByTime = mutableMapOf<String, MutableList<String>>()

                for (statusSnapshot in snapshot.children) {
                    val time = statusSnapshot.child("time").getValue(String::class.java)
                    val status = statusSnapshot.child("status").getValue(String::class.java)

                    if (time != null && status != null && time.startsWith(date)) {
                        val timePart = time.split(" ")[1].split(":")
                        val formattedTime = "${timePart[0]}:${timePart[1]}"

                        statusesByTime.computeIfAbsent(formattedTime) { mutableListOf() }.add(status) // Group statuses
                    }
                }

                // Add grouped statuses to the table
                if (statusesByTime.isEmpty()) {
                    resultTextView.text = "No status found for $date"
                } else {
                    resultTextView.text = "Statuses for $date:"
                    for ((time, statuses) in statusesByTime) {
                        val tableRow = TableRow(this@HistoryActivity)
                        tableRow.layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )

                        val timeTextView = TextView(this@HistoryActivity)
                        timeTextView.text = time
                        timeTextView.setPadding(8, 8, 8, 8)

                        val statusLayout = LinearLayout(this@HistoryActivity)
                        statusLayout.orientation = LinearLayout.HORIZONTAL
                        statusLayout.setPadding(8, 8, 8, 8)

                        // Process each status and its colors
                        statuses.forEach { status ->
                            val (color, colorName) = getColorForStatus(status)

                            // Border for the colorBox
                            val border = GradientDrawable().apply {
                                setColor(color)
                                setStroke(4, android.graphics.Color.BLACK)
                                cornerRadius = 8f
                            }

                            val colorBoxContainer = View(this@HistoryActivity).apply {
                                layoutParams = LinearLayout.LayoutParams(60, 60).apply {
                                    setMargins(8, 0, 8, 0)
                                }
                                background = border
                            }

                            statusLayout.addView(colorBoxContainer)

                            // Display the (ColorName)
                            val statusTextView = TextView(this@HistoryActivity).apply {
                                text = "($colorName)"
                                setPadding(8, 0, 8, 0)
                            }
                            statusLayout.addView(statusTextView)
                        }

                        tableRow.addView(timeTextView)
                        tableRow.addView(statusLayout)
                        tableLayout.addView(tableRow)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistoryActivity, "Error: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("HistoryActivity", "Firebase error: ${error.message}")
            }
        })
    }

    // Get both color and name from the status map
    private fun getColorForStatus(status: String): Pair<Int, String> {
        val statusLower = status.lowercase()
        return statusColorMap[statusLower] ?: Pair(android.graphics.Color.GRAY, "Unknown")
    }
}