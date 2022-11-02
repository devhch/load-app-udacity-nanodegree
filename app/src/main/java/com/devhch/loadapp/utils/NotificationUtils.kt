package com.devhch.loadapp.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory
import android.os.Build
import com.devhch.loadapp.DetailActivity
import com.devhch.loadapp.R

// Notification ID.
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = 0

/**
 * Builds and delivers the notification.
 *
 * @param messageBody, notification text.
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(
    applicationContext: Context,
    finaleName: String,
    messageBody: String,
    status: String
) {
    // Create intent
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.apply {
        putExtra(Constants.KEY_REPOSITORY_NAME, finaleName)
        putExtra(Constants.KEY_STATUS, status)
    }

    // Create PendingIntent
    val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // BitmapFactory
    val downloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_download
    )

    // Add style
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadImage)
        .bigLargeIcon(null)

    // get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        // Set title, text and icon to builder
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(downloadImage)
        .addAction(
            R.drawable.ic_download,
            applicationContext.getString(R.string.check_the_status),
            contentPendingIntent
        )
        // Set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
