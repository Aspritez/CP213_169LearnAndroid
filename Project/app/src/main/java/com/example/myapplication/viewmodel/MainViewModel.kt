package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.DataStoreManager
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.data.model.GameMode
import com.example.myapplication.data.model.Score
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ===== MainViewModel =====
 * ViewModel หลักของแอป จัดการ state ระดับ app-wide
 *
 * เกี่ยวข้องกับ:
 *   - DataStoreManager → อ่าน/เขียน settings และ scores
 *   - MainActivity (FocusShotApp) → สร้าง instance และ expose state ให้ทุกหน้า
 *   - HomeScreen → อ่าน playerName, selectedMode; เขียน updatePlayerName, updateSelectedMode
 *   - SettingsScreen → อ่าน appSettings; เขียน updateSettings
 *   - ScoreboardScreen → อ่าน scores
 *   - AudioController → อ่าน appSettings เพื่ออัปเดตเสียง
 */
class MainViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    /**
     * ===== App Settings Flow =====
     * การตั้งค่าแอปแบบ reactive — เมื่อ DataStore เปลี่ยน, UI จะอัปเดตอัตโนมัติ
     */
    val appSettings: StateFlow<AppSettings> = dataStoreManager.appSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    /**
     * ===== Scores Flow =====
     * คะแนนทั้งหมดแบบ reactive — ใช้ใน ScoreboardScreen
     */
    val scores: StateFlow<List<Score>> = dataStoreManager.scoresFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * ===== Player Name =====
     * ชื่อผู้เล่น – เก็บใน memory (ไม่ persist)
     * ใช้ใน: HomeScreen (input field), GameViewModel/GyroscopeViewModel (บันทึกคะแนน)
     */
    private val _playerName = MutableStateFlow("")
    val playerName = _playerName.asStateFlow()

    /**
     * ===== Selected Game Mode =====
     * โหมดเกมที่เลือก – ใช้ใน HomeScreen dropdown
     * ส่งไปยัง navigation เพื่อเปิดหน้าเกมที่ถูกต้อง
     */
    private val _selectedMode = MutableStateFlow(GameMode.GRIDSHOT)
    val selectedMode = _selectedMode.asStateFlow()

    fun updatePlayerName(name: String) {
        _playerName.value = name
    }

    fun updateSelectedMode(mode: GameMode) {
        _selectedMode.value = mode
    }

    /**
     * ===== Update Settings =====
     * บันทึกการตั้งค่าใหม่ลง DataStore
     * ถูกเรียกจาก: SettingsScreen ผ่าน onSettingsChanged callback
     * รองรับ field ใหม่: sfxSound, gyroSensitivity
     */
    fun updateSettings(settings: AppSettings) {
        viewModelScope.launch {
            dataStoreManager.updateAppSettings(settings)
        }
    }
}
