package app.sleeptracker.controller
import app.sleeptracker.configuration.AppConfig
import app.sleeptracker.domain.SleepData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SleepDataController {
    private val client = OkHttpClient()

    fun sendSleepData(sleepData: SleepData) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = JSONObject()

            val accelerometerJsonArray = sleepData.accelerometerReadings.map {
                JSONObject().apply {
                    put("x", it[0])
                    put("y", it[1])
                    put("z", it[2])
                    put("timestamp", it[3])
                }
            }
            json.put("accelerometerReadings", accelerometerJsonArray)

            val heartRateJsonArray = sleepData.heartRateReadings.map {
                JSONObject().apply {
                    put("heartRate", it[0])
                    put("timestamp", it[1])
                }
            }
            json.put("heartRateReadings", heartRateJsonArray)
            json.put("duration", sleepData.duration)

            val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(AppConfig.BASE_URL + AppConfig.API_SLEEP_DATA_ENDPOINT)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        println("Sleep data sent successfully!")
                    } else {
                        println("Failed to send sleep data: ${response.code}")
                    }
                }
            })
        }
    }
}