package com.ars.storage.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Disk-based artwork cache. Stores extracted album art as compressed JPEG files.
 * Key is derived from the track URI's hash so it survives across app restarts.
 */
@Singleton
class ArtworkCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cacheDir: File = File(context.cacheDir, "artwork").apply { mkdirs() }
    private val maxCacheSizeBytes = 50L * 1024 * 1024 // 50 MB

    fun get(uri: String): Bitmap? {
        val file = cacheFile(uri)
        if (!file.exists()) return null
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            Timber.w(e, "Failed to decode cached artwork for $uri")
            null
        }
    }

    fun put(uri: String, bitmap: Bitmap) {
        trimIfNeeded()
        val file = cacheFile(uri)
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to cache artwork for $uri")
        }
    }

    fun getFilePath(uri: String): String? {
        val file = cacheFile(uri)
        return if (file.exists()) file.absolutePath else null
    }

    fun clear() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    private fun cacheFile(uri: String): File {
        val hash = uri.hashCode().toString().replace("-", "n")
        return File(cacheDir, "$hash.jpg")
    }

    private fun trimIfNeeded() {
        val files = cacheDir.listFiles() ?: return
        val totalSize = files.sumOf { it.length() }
        if (totalSize < maxCacheSizeBytes) return

        // Delete oldest files until under limit
        files.sortedBy { it.lastModified() }.forEach { file ->
            file.delete()
            val remaining = cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
            if (remaining < maxCacheSizeBytes * 0.75) return
        }
    }
}
