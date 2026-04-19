package com.example.myapplication.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * ===== AudioController =====
 * จัดการเสียงทั้งหมดของแอป (SFX + Music)
 *
 * เกี่ยวข้องกับ:
 *   - AppSettings.sfxSound → กำหนดเสียงที่จะเล่น
 *   - AppSettings.sfxEnabled / musicEnabled → เปิด/ปิดเสียง
 *   - MainActivity → สร้าง instance และส่งไปยัง Composable
 *   - GameScreen / GyroscopeGameScreen → เรียก playSfx() เมื่อยิงเป้าโดน
 *   - SettingsScreen → เรียก previewSfx() เพื่อทดลองฟังเสียง
 */
class AudioController(private val context: Context) {

    private var sfxEnabled: Boolean = true
    private var musicEnabled: Boolean = true
    private var currentSfxSound: String = "pop"  // เสียง SFX ที่ใช้ปัจจุบัน

    // ToneGenerator สำหรับสร้างเสียง SFX (ไม่ต้องใช้ไฟล์เสียงภายนอก)
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    /**
     * ===== Sound Effect Mapping =====
     * แผนที่เสียง 5 แบบ → ToneGenerator tone type
     * แต่ละเสียงมีความถี่/รูปแบบต่างกัน:
     * - pop: เสียงสั้นคม (PROP_BEEP)
     * - bell: เสียงระฆัง (SUP_INTERCEPT)
     * - drip: เสียงหยดน้ำ (CDMA_ALERT_CALL_GUARD)
     * - blip: เสียง blip สั้น (PROP_ACK)
     * - ting: เสียง ting สูง (SUP_CONFIRM)
     */
    private val sfxToneMap: Map<String, Int> = mapOf(
        "pop" to ToneGenerator.TONE_PROP_BEEP,
        "bell" to ToneGenerator.TONE_SUP_INTERCEPT,
        "drip" to ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
        "blip" to ToneGenerator.TONE_PROP_ACK,
        "ting" to ToneGenerator.TONE_SUP_CONFIRM
    )

    /**
     * ===== Update Settings =====
     * อัปเดตค่าเปิด/ปิดเสียง — ถูกเรียกจาก MainActivity เมื่อ appSettings เปลี่ยน
     * (ผ่าน DisposableEffect ใน FocusShotApp)
     */
    fun updateSettings(sfx: Boolean, music: Boolean) {
        sfxEnabled = sfx
        musicEnabled = music
        if (!musicEnabled) {
            stopMusic()
        } else {
            playMusic()
        }
    }

    /**
     * ===== Update SFX Sound =====
     * เปลี่ยนเสียง SFX ที่จะเล่น — ถูกเรียกจาก MainActivity เมื่อ appSettings.sfxSound เปลี่ยน
     */
    fun updateSfxSound(sound: String) {
        currentSfxSound = sound
    }

    /**
     * ===== Play SFX =====
     * เล่นเสียง SFX ตาม currentSfxSound
     * ถูกเรียกจาก: GameScreen (onTargetClick), GyroscopeGameScreen (onFire เมื่อยิงโดน)
     */
    fun playSfx() {
        if (sfxEnabled) {
            val toneType = sfxToneMap[currentSfxSound] ?: ToneGenerator.TONE_PROP_BEEP
            toneGenerator.startTone(toneType, 80) // เล่นเสียง 80ms
        }
    }

    /**
     * ===== Preview SFX =====
     * เล่นเสียง SFX ตาม sound ที่ระบุ (ไม่สนใจ sfxEnabled)
     * ถูกเรียกจาก: SettingsScreen เพื่อทดลองฟังก่อนเลือก
     */
    fun previewSfx(sound: String) {
        val toneType = sfxToneMap[sound] ?: ToneGenerator.TONE_PROP_BEEP
        toneGenerator.startTone(toneType, 150) // เล่นเสียง 150ms สำหรับ preview
    }

    fun playMusic() {
        if (musicEnabled) {
            // ในแอปจริงจะใช้ MediaPlayer กับไฟล์ MP3 ใน res/raw
            Log.d("AudioController", "Simulating Background Music PLAY")
        }
    }

    fun stopMusic() {
        Log.d("AudioController", "Simulating Background Music STOP")
    }

    fun release() {
        toneGenerator.release()
        stopMusic()
    }
}
