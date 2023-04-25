package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
    private var projectSelected: String = ""

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
            projectSelected = getString(R.string.radio_glide).substringBefore(" -")
        }
        loadAppRadioButton.setOnClickListener {
            url = getString(R.string.url_loadapp)
            projectSelected = getString(R.string.radio_loadapp).substringBefore(" -")
        }
        retrofitRadioButton.setOnClickListener {
            url = getString(R.string.url_retrofit)
            projectSelected = getString(R.string.radio_retrofit).substringBefore(" -")
        }

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.createChannel()

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

            customButton.downloading = false

            notificationManager.sendNotification()
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

        customButton.downloading = true
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 0
    }

    private fun NotificationManager.createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.apply {
                enableLights(false)
                enableVibration(false)
                description = getString(R.string.notification_channel_description)
            }

            createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun NotificationManager.sendNotification() {
        // Explicit intent with the name of the activity to launch
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)

        // Pending intent containing the intent defined above and assigned to the notification
        // Pending intent is needed cause the app must be launched by another app or the system
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description, projectSelected))
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.notification_button),
                contentPendingIntent
            )

        notify(NOTIFICATION_ID, builder.build())
    }
}
