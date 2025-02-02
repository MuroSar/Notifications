package com.murosar.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.murosar.notifications.ui.theme.NotificationsTheme

class MainActivity : ComponentActivity() {

    private lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val permissionState = remember { mutableStateOf(isNotificationPermissionGranted(this)) }

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        permissionState.value = isGranted
                    }

                    LaunchedEffect(Unit) {
                        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                            override fun onChange(selfChange: Boolean) {
                                permissionState.value = isNotificationPermissionGranted(this@MainActivity)
                            }
                        }
                        contentResolver.registerContentObserver(
                            Settings.Secure.getUriFor("enabled_notification_listeners"),
                            true,
                            observer
                        )
                    }

                    MainScreen(
                        permissionState = permissionState.value,
                        askPermission = {
                            if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        },
                        notificationLauncher = {
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        })
                }
            }
        }

        createNotificationChannel()
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My notification title")
            .setContentText("My notification content text")
            .setSmallIcon(R.drawable.baseline_stars_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager = NotificationManagerCompat.from(this)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableLights(true)
                lightColor = Color.RED
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private val CHANNEL_ID = "CHANNEL_ID"
        private val CHANNEL_NAME = "CHANNEL_NAME"
        private val NOTIFICATION_ID = 0
    }
}