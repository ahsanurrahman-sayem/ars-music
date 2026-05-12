package com.ars.player.focus

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

enum class FocusResult { GRANTED, DENIED, LOSS, LOSS_TRANSIENT, DUCK }

/**
 * Smart audio focus handling that respects gaming and other high-priority audio.
 * Ducking is preferred over immediate pause for brief interruptions (e.g. navigation).
 */
@Singleton
class AudioFocusHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var focusRequest: AudioFocusRequest? = null
    private var onFocusChange: ((FocusResult) -> Unit)? = null

    private val focusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Timber.d("Audio focus: GAIN")
                onFocusChange?.invoke(FocusResult.GRANTED)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                Timber.d("Audio focus: LOSS")
                onFocusChange?.invoke(FocusResult.LOSS)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Timber.d("Audio focus: LOSS_TRANSIENT")
                onFocusChange?.invoke(FocusResult.LOSS_TRANSIENT)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Timber.d("Audio focus: DUCK")
                onFocusChange?.invoke(FocusResult.DUCK)
            }
        }
    }

    fun requestFocus(onChange: (FocusResult) -> Unit): Boolean {
        onFocusChange = onChange
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(false) // prefer duck over pause
                .setOnAudioFocusChangeListener(focusListener)
                .build()
            focusRequest = request
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusListener)
        }
        onFocusChange = null
    }
}
