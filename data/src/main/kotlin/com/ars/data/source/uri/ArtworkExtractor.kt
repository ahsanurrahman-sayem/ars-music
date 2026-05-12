package com.ars.data.source.uri

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtworkExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val artworkDir: File by lazy {
        File(context.filesDir, "artwork").also { it.mkdirs() }
    }

    /**
     * Extracts embedded art from the audio file and saves it to the app's private storage.
     * Returns the path to the saved art file, or null if no art found.
     */
    fun extractAndCache(uri: Uri, trackId: Long): String? {
        val outFile = File(artworkDir, "art_$trackId.jpg")
        if (outFile.exists()) return outFile.absolutePath

        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val art = retriever.embeddedPicture ?: return null
            outFile.writeBytes(art)
            outFile.absolutePath
        } catch (e: Exception) {
            Timber.e(e, "Failed to extract artwork from $uri")
            null
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }
    }

    fun deleteArtwork(trackId: Long) {
        File(artworkDir, "art_$trackId.jpg").delete()
    }
}
