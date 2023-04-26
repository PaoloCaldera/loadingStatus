package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    // Layout references in code
    private lateinit var filenameText: TextView
    private lateinit var statusText: TextView
    private lateinit var okButton: Button

    // Notification manager reference for the whole activity
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        // Get intent extras
        val extraParams = intent.extras!!

        // Layout inflation
        filenameText = findViewById(R.id.filename_text)
        statusText = findViewById(R.id.status_text)
        okButton = findViewById(R.id.ok_button)

        // Set the correct texts in the layout
        filenameText.text = extraParams.getString(EXTRA_PROJECT_SELECTED)
        statusText.text = extraParams.getString(EXTRA_STATUS)

        // Cancel the notification that has been clicked
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancel(extraParams.getInt(EXTRA_NOTIFICATION_ID))

        // Go back to the MainActivity instance that already exists in the background
        okButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    companion object {
        private const val EXTRA_PROJECT_SELECTED = "project_extra_string"
        private const val EXTRA_STATUS = "status_extra_string"
        private const val EXTRA_NOTIFICATION_ID = "notificationId_extra_int"
    }
}