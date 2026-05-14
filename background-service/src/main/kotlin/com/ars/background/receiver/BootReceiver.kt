package com.ars.background.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Receives BOOT_COMPLETED. Currently only logs; can be extended to
 * restore last session state or pre-warm the service.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Boot completed — ArSync ready")
            // No aggressive startup; let the user open the app.
        }
    }
}
