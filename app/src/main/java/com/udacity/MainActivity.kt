package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var radioGroup: RadioGroup
    private lateinit var glideRadioButton: RadioButton
    private lateinit var loadAppRadioButton: RadioButton
    private lateinit var retrofitRadioButton: RadioButton
    private lateinit var customButton: LoadingButton

    private var url = URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        radioGroup = findViewById(R.id.download_options_radioGroup)
        glideRadioButton = findViewById(R.id.glide_radioButton)
        loadAppRadioButton = findViewById(R.id.loadapp_radioButton)
        retrofitRadioButton = findViewById(R.id.retrofit_radioButton)
        customButton = findViewById(R.id.custom_button)

        glideRadioButton.setOnClickListener {
            url = getString(R.string.url_glide)
        }
        loadAppRadioButton.setOnClickListener {
            url = getString(R.string.url_loadapp)
        }
        retrofitRadioButton.setOnClickListener {
            url = getString(R.string.url_retrofit)
        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        customButton.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this, getString(R.string.toast_message), Toast.LENGTH_SHORT).show()
            else
                download(url)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
