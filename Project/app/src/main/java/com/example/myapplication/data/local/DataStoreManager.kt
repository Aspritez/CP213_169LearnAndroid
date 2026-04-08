package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.data.model.Score
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "focusshot_prefs")

class DataStoreManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        val SCORES_KEY = stringPreferencesKey("scores_key")
        val MUSIC_KEY = booleanPreferencesKey("music_key")
        val SFX_KEY = booleanPreferencesKey("sfx_key")
        val TARGET_COLOR_KEY = stringPreferencesKey("target_color_key")
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            musicEnabled = preferences[MUSIC_KEY] ?: true,
            sfxEnabled = preferences[SFX_KEY] ?: true,
            targetColorHex = preferences[TARGET_COLOR_KEY] ?: "#E63946"
        )
    }

    suspend fun updateAppSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[MUSIC_KEY] = settings.musicEnabled
            preferences[SFX_KEY] = settings.sfxEnabled
            preferences[TARGET_COLOR_KEY] = settings.targetColorHex
        }
    }

    val scoresFlow: Flow<List<Score>> = context.dataStore.data.map { preferences ->
        val json = preferences[SCORES_KEY] ?: "[]"
        val type = object : TypeToken<List<Score>>() {}.type
        gson.fromJson(json, type)
    }

    suspend fun saveScore(score: Score) {
        context.dataStore.edit { preferences ->
            val json = preferences[SCORES_KEY] ?: "[]"
            val type = object : TypeToken<List<Score>>() {}.type
            val currentScores: MutableList<Score> = gson.fromJson(json, type)
            currentScores.add(score)
            // Keep top 100 or sort
            currentScores.sortByDescending { it.score }
            val newJson = gson.toJson(currentScores)
            preferences[SCORES_KEY] = newJson
        }
    }
}
