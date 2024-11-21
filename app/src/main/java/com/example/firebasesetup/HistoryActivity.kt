package com.example.firebasesetup

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val daySpinner: Spinner = findViewById(R.id.daySpinner)
        val monthSpinner: Spinner = findViewById(R.id.monthSpinner)
        val yearSpinner: Spinner = findViewById(R.id.yearSpinner)

        val calendar = Calendar.getInstance()
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayYear = calendar.get(Calendar.YEAR)

        val days = (1..31).toList().map { it.toString() }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = dayAdapter
        daySpinner.setSelection(todayDay - 1)

        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter
        monthSpinner.setSelection(todayMonth)


        val years = (1900..2035).toList().map { it.toString() }
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter
        yearSpinner.setSelection(years.indexOf(todayYear.toString()))

        val Back=findViewById<Button>(R.id.Back)

        Back.setOnClickListener{
            val intent = Intent(this, ClientHomeActivity::class.java)
            startActivity(intent)
        }
    }
}
