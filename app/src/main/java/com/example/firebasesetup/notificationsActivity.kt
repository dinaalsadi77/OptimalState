package com.example.firebasesetup

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class notificationsActivity : AppCompatActivity() {
    private lateinit var notificationSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications)

        notificationSwitch = findViewById(R.id.notification_switch)

        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val isNotificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        notificationSwitch.isChecked = isNotificationsEnabled

        updateSwitchColors(isNotificationsEnabled)

        createNotificationChannel()

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("notifications_enabled", isChecked)
            editor.apply()

            updateSwitchColors(isChecked)

            if (isChecked) {
                sendTestNotification()
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSwitchColors(isEnabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val thumbColor = if (isEnabled) {
                ContextCompat.getColor(this, R.color.blue)
            } else {
                ContextCompat.getColor(this, android.R.color.darker_gray)
            }
            val trackColor = if (isEnabled) {
                ContextCompat.getColor(this, R.color.light_blue)
            } else {
                ContextCompat.getColor(this, android.R.color.darker_gray)
            }
            notificationSwitch.thumbTintList = ColorStateList.valueOf(thumbColor)
            notificationSwitch.trackTintList = ColorStateList.valueOf(trackColor)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Default Channel"
            val descriptionText = "Channel for default notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("default_channel", channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTestNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Test Notification")
            .setContentText("Notifications have been enabled.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}