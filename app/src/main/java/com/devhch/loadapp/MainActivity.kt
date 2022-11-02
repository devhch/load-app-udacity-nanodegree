package com.devhch.loadapp

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.devhch.loadapp.databinding.ActivityMainBinding
import com.devhch.loadapp.utils.Constants
import com.devhch.loadapp.utils.sendNotification
import java.io.File

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    // Set Values to these variable when the user checked a RadioButton
    private var checkedGitHubRepository: String? = null
    private var checkedGitHubFileName: String? = null

    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Download Button OnClickListener
        loadingButton = binding.included.downloadButton
        loadingButton.setLoadingButtonState(ButtonState.Completed)
        loadingButton.setOnClickListener { download() }

        // Glide Radio Button OnClickListener
        binding.included.glideRadioButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) {
                checkedGitHubRepository = getString(R.string.glide_github_repository_link)
                checkedGitHubFileName = getString(R.string.glide_title)
            }
        }

        // Load Radio Button OnClickListener
        binding.included.loadAppRadioButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) {
                checkedGitHubRepository = getString(R.string.load_app_github_repository_link)
                checkedGitHubFileName = getString(R.string.load_app_title)
            }
        }

        // Retrofit Radio Button OnClickListener
        binding.included.retrofitRadioButton.setOnClickListener { view ->
            if ((view as RadioButton).isChecked) {
                checkedGitHubRepository = getString(R.string.retrofit_github_repository_link)
                checkedGitHubFileName = getString(R.string.retrofit_title)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent!!.action
            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    // DownloadManager Query
                    val query = DownloadManager.Query()

                    // set Filter By Id
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))

                    // DownloadManager
                    val manager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                    // Cursor
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            // get cursorColumnIndex and check if it is greater than 0
                            val cursorColumnIndex =
                                cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            val columnIndex = if (cursorColumnIndex >= 0) cursorColumnIndex else 0
                            val status = cursor.getInt(columnIndex)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                loadingButton.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    applicationContext,
                                    checkedGitHubFileName.toString(),
                                    String.format(
                                        context.getString(R.string.notification_description),
                                        checkedGitHubFileName.toString()
                                    ),
                                    context.getString(R.string.downloaded_successfully)
                                )
                            } else {
                                loadingButton.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    applicationContext,
                                    checkedGitHubFileName.toString(),
                                    checkedGitHubFileName.toString(),
                                    context.getString(R.string.downloaded_failed)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        // Set ButtonState to Clicked
        loadingButton.setLoadingButtonState(ButtonState.Clicked)

        if (checkedGitHubRepository != null) {
            // Set ButtonState to Loading
            loadingButton.setLoadingButtonState(ButtonState.Loading)

            // Initialize notificationManager
            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager


            // createChannel
            createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name)
            )

            val file = File(getExternalFilesDir(null), "/${Constants.KEY_GITHUB_REPOSITORIES}")
            if (!file.exists()) {
                file.mkdirs()
            }

            // Path
            val splitName = checkedGitHubFileName?.split(' ')?.get(0)
            val path = "/${Constants.KEY_GITHUB_REPOSITORIES}/${splitName}.zip"

            /// Request download
            val request =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    DownloadManager.Request(Uri.parse(checkedGitHubRepository))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            path
                        )
                } else {
                    DownloadManager.Request(Uri.parse(checkedGitHubRepository))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            path
                        )
                }

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            // Enqueue puts the download request in the queue.
            downloadID = downloadManager.enqueue(request)
        } else {
            // Set ButtonState to Completed
            loadingButton.setLoadingButtonState(ButtonState.Completed)

            // Show Toast
            Toast.makeText(this, getString(R.string.please_select_file), Toast.LENGTH_SHORT).show()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.notification_channel_description)

            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


}
