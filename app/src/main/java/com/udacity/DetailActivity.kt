package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_main.*

class DetailActivity : AppCompatActivity() {

    private lateinit var filenameText: TextView
    private lateinit var statusText: TextView
    private lateinit var okButton: Button

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extraParams = intent.extras!!

        filenameText = findViewById(R.id.filename_text)
        statusText = findViewById(R.id.status_text)
        okButton = findViewById(R.id.ok_button)

        filenameText.text = extraParams.getString(EXTRA_PROJECT_SELECTED)
        statusText.text = extraParams.getString(EXTRA_STATUS)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancel(extraParams.getInt(EXTRA_NOTIFICATION_ID))

        okButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        private const val EXTRA_PROJECT_SELECTED = "project_extra_string"
        private const val EXTRA_STATUS = "status_extra_string"
        private const val EXTRA_NOTIFICATION_ID = "notificationId_extra_int"
    }
}