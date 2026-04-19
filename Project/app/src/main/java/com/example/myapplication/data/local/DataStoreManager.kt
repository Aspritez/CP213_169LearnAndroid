package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.data.model.Score
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ===== DataStore Extension =====
 * สร้าง DataStore instance สำหรับเก็บ preferences ทั้งหมดของแอป
 * ใช้ชื่อ "focusshot_prefs" เป็นไฟล์เก็บข้อมูล
 */
val Context.dataStore by preferencesDataStore(name = "focusshot_prefs")

/**
 * ===== DataStoreManager =====
 * จัดการการอ่าน/เขียนข้อมูลลง DataStore (local storage)
 * เกี่ยวข้องกับ:
 *   - Models.kt → ใช้ AppSettings และ Score data class
 *   - MainViewModel → อ่าน settings/scores ผ่าน Flow
 *   - GameViewModel / GyroscopeViewModel → บันทึก score หลังจบเกม
 */
class DataStoreManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        // ===== Preference Keys =====
        val SCORES_KEY = stringPreferencesKey("scores_key")              // คะแนนทั้งหมด (JSON string)
        val MUSIC_KEY = booleanPreferencesKey("music_key")               // เปิด/ปิดเพลง
        val SFX_KEY = booleanPreferencesKey("sfx_key")                   // เปิด/ปิดเสียง SFX
        val TARGET_COLOR_KEY = stringPreferencesKey("target_color_key")  // สี hex ของเป้าหมาย
        val SFX_SOUND_KEY = stringPreferencesKey("sfx_sound_key")        // ชื่อเสียง SFX ที่เลือก (pop/bell/drip/blip/ting)
        val GYRO_SENSITIVITY_KEY = floatPreferencesKey("gyro_sensitivity_key") // ค่า sensitivity ของ gyroscope
    }

    /**
     * ===== App Settings Flow =====
     * อ่านการตั้งค่าจาก DataStore แบบ reactive (Flow)
     * เมื่อค่าเปลี่ยน → MainViewModel จะได้รับค่าใหม่อัตโนมัติ
     */
    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            musicEnabled = preferences[MUSIC_KEY] ?: true,
            sfxEnabled = preferences[SFX_KEY] ?: true,
            targetColorHex = preferences[TARGET_COLOR_KEY] ?: "#E63946",
            sfxSound = preferences[SFX_SOUND_KEY] ?: "pop",
            gyroSensitivity = preferences[GYRO_SENSITIVITY_KEY] ?: 3.0f
        )
    }

    /**
     * ===== Update App Settings =====
     * บันทึกการตั้งค่าทั้งหมดลง DataStore
     * ถูกเรียกจาก: MainViewModel.updateSettings() ← SettingsScreen
     */
    suspend fun updateAppSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[MUSIC_KEY] = settings.musicEnabled
            preferences[SFX_KEY] = settings.sfxEnabled
            preferences[TARGET_COLOR_KEY] = settings.targetColorHex
            preferences[SFX_SOUND_KEY] = settings.sfxSound
            preferences[GYRO_SENSITIVITY_KEY] = settings.gyroSensitivity
        }
    }

    /**
     * ===== Scores Flow =====
     * อ่านคะแนนทั้งหมดจาก DataStore แบบ reactive
     * ใช้ Gson แปลง JSON → List<Score>
     * เกี่ยวข้องกับ: ScoreboardScreen (แสดง leaderboard), MainViewModel (expose flow)
     */
    val scoresFlow: Flow<List<Score>> = context.dataStore.data.map { preferences ->
        val json = preferences[SCORES_KEY] ?: "[]"
        val type = object : TypeToken<List<Score>>() {}.type
        gson.fromJson(json, type)
    }

    /**
     * ===== Save Score =====
     * บันทึกคะแนนรอบใหม่ลง DataStore
     * - เพิ่ม score ใหม่เข้า list
     * - เรียงจากมากไปน้อย
     * ถูกเรียกจาก: GameViewModel.endGame() / GyroscopeViewModel.endGame()
     */
    suspend fun saveScore(score: Score) {
        context.dataStore.edit { preferences ->
            val json = preferences[SCORES_KEY] ?: "[]"
            val type = object : TypeToken<List<Score>>() {}.type
            val currentScores: MutableList<Score> = gson.fromJson(json, type)
            currentScores.add(score)
            // เรียงคะแนนจากมากไปน้อย
            currentScores.sortByDescending { it.score }
            val newJson = gson.toJson(currentScores)
            preferences[SCORES_KEY] = newJson
        }
    }
}
