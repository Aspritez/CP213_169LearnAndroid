package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.DataStoreManager
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.data.model.Score
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = dataStoreManager.appSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    val scores: StateFlow<List<Score>> = dataStoreManager.scoresFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _playerName = MutableStateFlow("")
    val playerName = _playerName.asStateFlow()

    fun updatePlayerName(name: String) {
        _playerName.value = name
    }

    fun updateSettings(musicEnabled: Boolean, sfxEnabled: Boolean, targetColorHex: String) {
        viewModelScope.launch {
            dataStoreManager.updateAppSettings(
                AppSettings(
                    musicEnabled = musicEnabled,
                    sfxEnabled = sfxEnabled,
                    targetColorHex = targetColorHex
                )
            )
        }
    }
}
