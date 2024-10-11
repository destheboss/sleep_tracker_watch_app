package app.sleeptracker.business.managers

import android.content.Context
import android.hardware.SensorEventListener
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.util.Log
import app.sleeptracker.controller.SleepDataController
import app.sleeptracker.domain.SleepData

class SensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var heartRateSensor: Sensor? = null
    private val sleepDataController = SleepDataController()

    private var accelerometerReadings = mutableListOf<FloatArray>()
    private var heartRateReadings = mutableListOf<FloatArray>()
    private var startTime: Long = 0

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    }

    fun startTracking() {
        startTime = System.currentTimeMillis()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopTracking() {
        sensorManager.unregisterListener(this)

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        val sleepData = SleepData(
            accelerometerReadings = accelerometerReadings,
            heartRateReadings = heartRateReadings,
            duration = duration
        )
        sleepDataController.sendSleepData(sleepData)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                accelerometerReadings.add(floatArrayOf(x, y, z, event.timestamp.toFloat()))
                Log.d("SleepSensorManager", "Accelerometer data: x=$x, y=$y, z=$z")
            }
            Sensor.TYPE_HEART_RATE -> {
                val heartRate = event.values[0]
                heartRateReadings.add(floatArrayOf(heartRate, event.timestamp.toFloat()))
                Log.d("SleepSensorManager", "Heart rate: $heartRate")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Optional: Handle sensor accuracy changes
    }
}