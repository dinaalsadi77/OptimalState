package com.example.firebasesetup

import android.app.DownloadManager
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method
import com.android.volley.Request


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

                // Create table headers
                createTableHeader()

                // Iterate through the users and add each client as a row
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    val clientName = userSnapshot.child("email").getValue(String::class.java)
                    val userRole = userSnapshot.child("role").getValue(String::class.java)

                    // Add clients to the table if role is "Client"
                    if (userRole == "Client") {
                        addClientToTable(userId, clientName)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RemoveClientActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createTableHeader() {
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
    }

    private fun addClientToTable(userId: String?, clientName: String?) {
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

    private fun deleteSelectedClients() {
        for (i in 1 until clientTable.childCount) {
            val tableRow = clientTable.getChildAt(i) as TableRow
            val radioButton = tableRow.getChildAt(1) as RadioButton
            val clientEmail = (tableRow.getChildAt(0) as TextView).text.toString()

            if (radioButton.isChecked) {
                val userId = radioButton.tag as? String

                if (userId != null) {
                    val url = "http://192.168.100.70:3000/deleteUser"  //server url for deleting the user
                    val jsonRequest = JSONObject().apply {
                        put("userId", userId)
                    }

                    // Create a Volley request to delete the user
                    val request = JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonRequest,
                        { response ->
                            try {
                                val success = response.getBoolean("success")
                                if (success) {
                                    Toast.makeText(this, "Client $clientEmail removed successfully.", Toast.LENGTH_SHORT).show()
                                    radioButton.isChecked = false
                                } else {
                                    val errorMsg = response.optString("error", "Unknown error occurred")
                                    Toast.makeText(this, "Failed to remove client: $errorMsg", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        { error ->
                            Toast.makeText(this, "Error communicating with server: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Add the request to the Volley request queue
                    Volley.newRequestQueue(this).add(request)
                }
            }
        }
    }
}