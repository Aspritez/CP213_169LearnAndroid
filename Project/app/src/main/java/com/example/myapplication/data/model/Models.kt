package com.example.myapplication.data.model

/**
 * ===== GameMode Enum =====
 * กำหนดโหมดเกมที่รองรับ
 * - GRIDSHOT: โหมดกดเป้าบน grid 3x4 (โหมดเดิม)
 * - GYROSCOPE: โหมดฝึก gyroscope โดยเคลื่อนเครื่องเล็งเป้า
 * ใช้ใน: HomeScreen (เลือกโหมด), GameViewModel/GyroscopeViewModel (logic เกม),
 *         ScoreboardScreen (แยก tab), Score model (บันทึกโหมด)
 */
enum class GameMode {
    GRIDSHOT,
    GYROSCOPE
}

/**
 * ===== Score Data Class =====
 * เก็บข้อมูลคะแนนของผู้เล่นแต่ละรอบ
 * - gameMode: โหมดที่เล่น (GRIDSHOT / GYROSCOPE) → ใช้แยก leaderboard
 * - accuracy: ความแม่นยำ (0.0-100.0%) → ใช้เฉพาะโหมด GYROSCOPE
 * เกี่ยวข้องกับ: DataStoreManager (บันทึก/โหลด), ScoreboardScreen (แสดงผล),
 *               GameViewModel & GyroscopeViewModel (คำนวณคะแนน)
 */
data class Score(
    val id: String = java.util.UUID.randomUUID().toString(),
    val playerName: String,
    val score: Int,
    val gameMode: String = GameMode.GRIDSHOT.name, // โหมดเกมที่เล่น (ใช้ String เพื่อ serialize ง่าย)
    val accuracy: Float = 100f,                     // ความแม่นยำ % (ใช้ใน Gyroscope mode)
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * ===== AppSettings Data Class =====
 * เก็บการตั้งค่าของแอป ← บันทึกลง DataStore
 * - targetColorHex: สี hex ของเป้าหมาย → SettingsScreen (input), GameScreen (แสดง)
 * - sfxSound: เสียง effect ที่เลือก → AudioController (เล่นเสียง), SettingsScreen (เลือก)
 * - gyroSensitivity: ความไวของ gyroscope → GyroscopeViewModel (คำนวณ offset), SettingsScreen (slider)
 */
data class AppSettings(
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val targetColorHex: String = "#E63946",
    val sfxSound: String = "pop",           // เสียง SFX: pop, bell, drip, blip, ting
    val gyroSensitivity: Float = 3.0f       // ค่า sensitivity ของ gyroscope (1.0 - 10.0, default 3.0)
)
