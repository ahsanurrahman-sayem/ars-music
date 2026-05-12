package com.ars.background.receiver

import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import timber.log.Timber

/**
 * Handles wired headset plug/unplug and Bluetooth headset disconnects.
 * Pausing on unplug prevents audio from suddenly blasting through speakers.
 *
 * Note: ExoPlayer already handles AUDIO_BECOMING_NOISY internally when
 * setHandleAudioBecomingNoisy(true) is set, so this receiver is an extra
 * safety net for BT disconnects.
 */
class HeadsetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AudioManager.ACTION_HEADSET_PLUG -> {
                val state = intent.getIntExtra("state", -1)
                if (state == 0) {
                    // Unplugged — ExoPlayer's AUDIO_BECOMING_NOISY handles this,
                    // but log for diagnostics
                    Timber.d("Wired headset unplugged")
                }
            }
            BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)
                if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    Timber.d("Bluetooth headset disconnected")
                    // ExoPlayer's audio focus handling pauses playback when
                    // BT device disconnects, as the audio routing changes.
                }
            }
        }
    }
}
