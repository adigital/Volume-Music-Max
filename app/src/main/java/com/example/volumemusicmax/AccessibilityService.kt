package com.example.volumemusicmax

import android.accessibilityservice.AccessibilityService
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()

        MainScope().launch(Dispatchers.Default) {
            delay(5000)
            setMaxVolume(isMinimum = false, isCoroutine = true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        setMaxVolume(isMinimum = false)
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action = event.action
        val keyCode = event.keyCode

        if (action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    setMaxVolume(isMinimum = true)
                    return true
                }

                KeyEvent.KEYCODE_VOLUME_UP -> {
                    setMaxVolume(isMinimum = false)
                    return true
                }
            }
        }

        return false
    }

    private fun setMaxVolume(isMinimum: Boolean, isCoroutine: Boolean = false) {
        try {
            val audioManager: AudioManager =
                applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (isMinimum) audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) else
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )

            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                audioManager.getStreamMinVolume(AudioManager.STREAM_ALARM),
                0
            )
            audioManager.setStreamVolume(
                AudioManager.STREAM_RING,
                audioManager.getStreamMinVolume(AudioManager.STREAM_RING),
                0
            )
            audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL),
                0
            )

            updateTotalUnsafeMilliseconds(contentResolver)

            if (!isCoroutine) {
                Toast.makeText(
                    applicationContext,
                    if (isMinimum) "Минимум громкости!" else "Максимум громкости!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            if (!isCoroutine) {
                Toast.makeText(
                    applicationContext,
                    "Volume Music Max: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateTotalUnsafeMilliseconds(contentResolver: ContentResolver?) {
        Settings.Secure.putInt(contentResolver, "unsafe_volume_music_active_ms", 1)
    }
}