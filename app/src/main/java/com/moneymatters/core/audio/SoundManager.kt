package com.moneymatters.core.audio

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SoundManager {
    private var toneGen: ToneGenerator? = null
    private var isMuted: Boolean = false

    /** Call once from Application.onCreate to pre-warm ToneGenerator off main thread */
    fun prewarm() {
        CoroutineScope(Dispatchers.IO).launch {
            getToneGen()
        }
    }

    @Synchronized
    private fun getToneGen(): ToneGenerator? {
        if (toneGen == null) {
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
            } catch (e: Exception) {
                Log.e("SoundManager", "Failed to initialize ToneGenerator", e)
            }
        }
        return toneGen
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isMuted(): Boolean = isMuted

    fun playNodeTapSound() {
        if (isMuted) return
        try {
            getToneGen()?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing tap sound", e)
        }
    }

    fun playModuleUnlockSound() {
        if (isMuted) return
        try {
            getToneGen()?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing unlock sound", e)
        }
    }

    fun playSuccessChime() {
        if (isMuted) return
        try {
            getToneGen()?.startTone(ToneGenerator.TONE_PROP_PROMPT, 150)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing success chime", e)
        }
    }
}
