package com.example.a169lablearnandroid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorTracker = SensorTracker(application)

    private val _sensorData = MutableStateFlow(SensorData())
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()

    fun startTracking() {
        sensorTracker.startListening(
            onSensorChanged = { x, y, z ->
                _sensorData.update { it.copy(x = x, y = y, z = z) }
            },
            onLocationChanged = { lat, lng ->
                _sensorData.update { it.copy(latitude = lat, longitude = lng) }
            }
        )
    }

    fun stopTracking() {
        sensorTracker.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}
