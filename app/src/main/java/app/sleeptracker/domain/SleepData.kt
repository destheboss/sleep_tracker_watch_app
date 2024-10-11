package app.sleeptracker.domain

data class SleepData(
    val accelerometerReadings: List<FloatArray>,
    val heartRateReadings: List<FloatArray>,
    val duration: Long
)