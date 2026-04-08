package com.example.myapplication.data.model

data class Score(
    val id: String = java.util.UUID.randomUUID().toString(),
    val playerName: String,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class AppSettings(
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val targetColorHex: String = "#E63946"
)
