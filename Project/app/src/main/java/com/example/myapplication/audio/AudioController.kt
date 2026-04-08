package com.example.myapplication.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

class AudioController(private val context: Context) {

    private var sfxEnabled: Boolean = true
    private var musicEnabled: Boolean = true
    
    // Simple ToneGenerator for SFX without needing external MP3s
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    fun updateSettings(sfx: Boolean, music: Boolean) {
        sfxEnabled = sfx
        musicEnabled = music
        if (!musicEnabled) {
            stopMusic()
        } else {
            playMusic()
        }
    }

    fun playSfx() {
        if (sfxEnabled) {
            // Play a quick beep
            toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 50)
        }
    }

    fun playMusic() {
        if (musicEnabled) {
            // In a real app, you would use MediaPlayer with an MP3 file in res/raw.
            // Since we don't have an asset, we just log it for now.
            // Example:
            // mediaPlayer = MediaPlayer.create(context, R.raw.bgm)
            // mediaPlayer?.isLooping = true
            // mediaPlayer?.start()
            Log.d("AudioController", "Simulating Background Music PLAY")
        }
    }

    fun stopMusic() {
        Log.d("AudioController", "Simulating Background Music STOP")
        // mediaPlayer?.stop()
        // mediaPlayer?.release()
        // mediaPlayer = null
    }

    fun release() {
        toneGenerator.release()
        stopMusic()
    }
}
