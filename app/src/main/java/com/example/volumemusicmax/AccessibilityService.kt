package com.example.volumemusicmax

import android.accessibilityservice.AccessibilityService
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()

        setMaxVolume()
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
                    setMaxVolume()
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    setMaxVolume()
                    return true
                }
            }
        }
        return false
    }

    private fun setMaxVolume() {
        try {
            val audioManager: AudioManager =
                applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )
//        audioManager.setStreamVolume(AudioManager.STREAM_ACCESSIBILITY, audioManager.getStreamMinVolume(AudioManager.STREAM_ACCESSIBILITY), 0)
            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                audioManager.getStreamMinVolume(AudioManager.STREAM_ALARM),
                0
            )
//        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, audioManager.getStreamMinVolume(AudioManager.STREAM_DTMF), 0)
//        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMinVolume(AudioManager.STREAM_NOTIFICATION), 0)
            audioManager.setStreamVolume(
                AudioManager.STREAM_RING,
                audioManager.getStreamMinVolume(AudioManager.STREAM_RING),
                0
            )
//        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, audioManager.getStreamMinVolume(AudioManager.STREAM_SYSTEM), 0)
            audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMinVolume(AudioManager.STREAM_VOICE_CALL),
                0
            )

            updateTotalUnsafeMilliseconds(contentResolver, 1)

            Toast.makeText(
                applicationContext,
                "Громкость установлена на максимум!",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Volume Music Max: $e",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateTotalUnsafeMilliseconds(contentResolver: ContentResolver?, value: Int) {
        Settings.Secure.putInt(contentResolver, "unsafe_volume_music_active_ms", value)
    }
}