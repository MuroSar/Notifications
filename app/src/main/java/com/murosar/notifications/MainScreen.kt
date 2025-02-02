package com.murosar.notifications

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.murosar.notifications.ui.theme.NotificationsTheme

@Composable
fun MainScreen(
    permissionState: Boolean,
    askPermission: () -> Unit,
    notificationLauncher: () -> Unit,
) {
//    val context = LocalContext.current
//    val permissionState = remember { mutableStateOf(isNotificationPermissionGranted(context)) }

//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        permissionState.value = isGranted
//    }
//
//    LaunchedEffect(Unit) {
//        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
//            override fun onChange(selfChange: Boolean) {
//                permissionState.value = isNotificationPermissionGranted(context)
//            }
//        }
//        context.contentResolver.registerContentObserver(
//            Settings.Secure.getUriFor("enabled_notification_listeners"),
//            true,
//            observer
//        )
//    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NotificationPermissionComponent(
            permissionState = permissionState,
            askPermission = askPermission,
        )
        Button(
            onClick = notificationLauncher,
            enabled = permissionState
        ) {
            Text(text = "Launch the notification")
        }
    }
}

@Composable
fun NotificationPermissionComponent(
    permissionState: Boolean,
    askPermission: () -> Unit,
) {
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (permissionState) "Notification permission granted ✅" else "Notification permission denied ❌",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    askPermission()
                }
            },
            enabled = !permissionState
        ) {
            Text("Ask for notification permission")
        }
    }
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NotificationsTheme {
        MainScreen(
            permissionState = true,
            askPermission = {},
            notificationLauncher = {},
        )
    }
}
