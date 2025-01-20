package com.example.diallerapp.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.diallerapp.R
import com.example.diallerapp.activities.CallActivity

class CallForegroundService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Create a pending intent for when the notification is clicked
        val pendingIntent = Intent(this, CallActivity::class.java)
        pendingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntentNotification = PendingIntent.getActivity(
            this,
            0,
            pendingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create Notification
        val notification = NotificationCompat.Builder(this, "call_service_channel")
            .setContentTitle("Active Call")
            .setContentText("Tap to return to call activity")
            .setSmallIcon(R.drawable.ic_call_icon) // Use a call icon here
            .setContentIntent(pendingIntentNotification)
            .setOngoing(true)
            .build()

        // Start Foreground service with the notification
        startForeground(1, notification)

        return START_STICKY // Service restart behavior when killed
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)// Stop the service
    }
}
