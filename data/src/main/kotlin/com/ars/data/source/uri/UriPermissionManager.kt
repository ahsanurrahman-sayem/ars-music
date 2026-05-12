package com.ars.data.source.uri

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Takes a persistent read permission on a content URI so the app can access
     * the file across reboots without copying it. This is the core of our
     * "link-by-reference, don't copy" strategy.
     */
    fun persist(uri: Uri): Boolean {
        return try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Timber.d("Persisted URI permission: $uri")
            true
        } catch (e: SecurityException) {
            Timber.w(e, "Cannot persist URI permission for $uri — will fall back to copy")
            false
        }
    }

    /**
     * Releases a previously persisted URI permission.
     * Called when the user removes a track from the library.
     */
    fun release(uri: Uri) {
        try {
            context.contentResolver.releasePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            Timber.w(e, "Could not release URI permission for $uri")
        }
    }

    /**
     * Returns true if the app currently holds a persisted read permission for this URI.
     */
    fun hasPermission(uri: Uri): Boolean {
        return context.contentResolver.persistedUriPermissions.any {
            it.uri == uri && it.isReadPermission
        }
    }

    /**
     * Returns true if the URI is still accessible (file exists / still readable).
     */
    fun isAccessible(uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
