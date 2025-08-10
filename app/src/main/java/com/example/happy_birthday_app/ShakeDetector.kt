package com.example.happy_birthday_app
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null

    private var lastUpdate: Long = 0
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    private val shakeThreshold = 800 // Adjust this threshold based on testing
    private val shakeDebounceTimeMs = 1000 // Only allow one shake per second

    init {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            lastUpdate = System.currentTimeMillis()
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Can be ignored for shake detection
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdate) > 100) { // Update only every 100ms
                val diffTime = (currentTime - lastUpdate)
                lastUpdate = currentTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > shakeThreshold) {
                    // Debounce: check if enough time has passed since the last shake
                    if (currentTime - lastShakeTime > shakeDebounceTimeMs) {
                        lastShakeTime = currentTime
                        onShake()
                    }
                }
                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }
    companion object { // To manage debounce for the callback
        private var lastShakeTime: Long = 0
    }
}
