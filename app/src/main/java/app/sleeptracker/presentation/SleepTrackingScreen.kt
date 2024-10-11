package app.sleeptracker.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import kotlinx.coroutines.launch
import app.sleeptracker.business.managers.SensorManager

class SleepTrackingScreen : ComponentActivity() {
    private lateinit var sleepSensorManager: SensorManager
    private var isTracking = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeTracking()
        } else {
            Toast.makeText(this, "Permission denied. Sleep tracking will not work.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        } else {
            initializeTracking()
        }

        setContent {
            var isTracking by remember { mutableStateOf(false) }

            MaterialTheme {
                ScalingLazyColumn {
                    item {
                        Button(onClick = {
                            isTracking = !isTracking
                            if (isTracking) {
                                startSleepTracking()
                            } else {
                                stopSleepTracking()
                            }
                        }) {
                            Text(if (isTracking) "Stop Tracking" else "Start Tracking")
                        }
                    }
                }
            }
        }
    }

    private fun initializeTracking() {
        sleepSensorManager = SensorManager(this)
    }

    override fun onPause() {
        super.onPause()
        stopSleepTracking()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSleepTracking()
    }

    private fun startSleepTracking() {
        lifecycleScope.launch {
            sleepSensorManager.startTracking()
        }
    }

    private fun stopSleepTracking() {
        lifecycleScope.launch {
            sleepSensorManager.stopTracking()
        }
    }
}