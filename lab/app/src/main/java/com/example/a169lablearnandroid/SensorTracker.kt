package com.example.a169lablearnandroid

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class SensorTracker(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var sensorListener: SensorEventListener? = null
    private var locationListener: LocationListener? = null

    @SuppressLint("MissingPermission")
    fun startListening(
        onSensorChanged: (Float, Float, Float) -> Unit,
        onLocationChanged: (Double, Double) -> Unit
    ) {
        if (sensorListener == null) {
            sensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                        onSensorChanged(event.values[0], event.values[1], event.values[2])
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            accelerometer?.let {
                sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
            }
        }

        if (locationListener == null) {
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    onLocationChanged(location.latitude, location.longitude)
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            try {
                // Request from both GPS and Network providers for better coverage
                val providers = locationManager.getProviders(true)
                for (provider in providers) {
                    locationManager.requestLocationUpdates(
                        provider,
                        2000L,
                        1f,
                        locationListener!!
                    )
                }
            } catch (e: Exception) {
                // Permission might not be granted yet
            }
        }
    }

    fun stopListening() {
        sensorListener?.let {
            sensorManager.unregisterListener(it)
            sensorListener = null
        }
        locationListener?.let {
            locationManager.removeUpdates(it)
            locationListener = null
        }
    }
}
