package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
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

    // Notification variables
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var currentNotificationId: Int = 0

    // Layout references in code
    private lateinit var radioGroup: RadioGroup
    private lateinit var glideRadioButton: RadioButton
    private lateinit var loadAppRadioButton: RadioButton
    private lateinit var retrofitRadioButton: RadioButton
    private lateinit var customButton: LoadingButton

    // Url and name of the project selected
    private var url = URL
    private var projectSelected: String = ""

    // Status of the download
    private var downloadStatus: DownloadStatus = DownloadStatus.SUCCESS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Layout inflation
        radioGroup = findViewById(R.id.download_options_radioGroup)
        glideRadioButton = findViewById(R.id.glide_radioButton)
        loadAppRadioButton = findViewById(R.id.loadapp_radioButton)
        retrofitRadioButton = findViewById(R.id.retrofit_radioButton)
        customButton = findViewById(R.id.custom_button)

        // Set the url and the project associated to the radio button selected
        glideRadioButton.setOnClickListener {
            url = getString(R.string.url_glide)
            projectSelected = getString(R.string.radio_glide)
        }
        loadAppRadioButton.setOnClickListener {
            url = getString(R.string.url_loadapp)
            projectSelected = getString(R.string.radio_loadapp)
        }
        retrofitRadioButton.setOnClickListener {
            url = getString(R.string.url_retrofit)
            projectSelected = getString(R.string.radio_retrofit)
        }

        // Create the notification manager channel
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.createChannel()

        // Register the broadcast receiver for the download completion
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Start the download when the custom button is clicked, or display a message on screen
        customButton.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1)
                Toast.makeText(this, getString(R.string.toast_message), Toast.LENGTH_SHORT).show()
            else
                download(url)
        }
    }

    /**
     * Broadcast receiver activated when the download is complete
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            // Set the download status
            downloadStatus =
                if (id == downloadID) DownloadStatus.SUCCESS
                else DownloadStatus.FAIL

            // Set the flag for the custom view animation to interrupt
            customButton.downloading = false

            // Increase the counter of the notificationID and send the notification
            currentNotificationId++
            notificationManager.sendNotification()

        }
    }

    /**
     * Prepare and execute the download operation, setting the state to true
     */
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

        downloadStatus = DownloadStatus.PROGRESS
        customButton.downloading = true
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val EXTRA_PROJECT_SELECTED = "project_extra_string"
        private const val EXTRA_STATUS = "status_extra_string"
        private const val EXTRA_NOTIFICATION_ID = "notificationId_extra_int"
    }

    /**
     * Create a notification channel with low importance
     */
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

    /**
     * Send the notification with intent extras and one action button
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun NotificationManager.sendNotification() {
        // Explicit intent with the name of the activity to launch
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.putExtra(EXTRA_PROJECT_SELECTED, projectSelected)
        contentIntent.putExtra(EXTRA_STATUS, downloadStatus.label)
        contentIntent.putExtra(EXTRA_NOTIFICATION_ID, currentNotificationId)

        // Pending intent containing the intent defined above and assigned to the notification
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            currentNotificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Action associated to the notification
        action = NotificationCompat.Action(
            R.drawable.ic_assistant_black_24dp,
            getString(R.string.notification_button),
            pendingIntent
        )

        // Notification builder
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(
                getString(
                    R.string.notification_description,
                    projectSelected.substringBefore(" -")
                )
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .addAction(action)

        // Notify the system bar with the notification created
        notify(currentNotificationId, builder.build())
    }
}
