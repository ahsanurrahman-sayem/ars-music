package com.ars.data.source.uri

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.ars.core.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class AudioMetadata(
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String,
    val durationMs: Long,
    val fileSizeBytes: Long,
    val mimeType: String,
    val trackNumber: Int,
    val year: Int,
    val bitrate: Int,
    val sampleRate: Int,
    val hasEmbeddedArt: Boolean
)

@Singleton
class UriMetadataExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun extract(uri: Uri): AudioMetadata? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?: uri.lastPathSegment?.removeSuffix(".mp3")?.removeSuffix(".flac")
                ?.removeSuffix(".m4a")?.removeSuffix(".ogg") ?: "Unknown"

            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"
            val albumArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST) ?: artist
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "audio/*"
            val trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.toIntOrNull() ?: 0
            val year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)?.toIntOrNull() ?: 0
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            val sampleRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toIntOrNull() ?: 44100
            val hasEmbeddedArt = retriever.embeddedPicture != null

            val fileSizeBytes = try {
                context.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
                    fd.statSize
                } ?: 0L
            } catch (e: Exception) { 0L }

            AudioMetadata(
                uri = uri.toString(),
                title = title,
                artist = artist,
                album = album,
                albumArtist = albumArtist,
                durationMs = durationMs,
                fileSizeBytes = fileSizeBytes,
                mimeType = mimeType,
                trackNumber = trackNumber,
                year = year,
                bitrate = bitrate,
                sampleRate = sampleRate,
                hasEmbeddedArt = hasEmbeddedArt
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to extract metadata for $uri")
            null
        } finally {
            try { retriever.release() } catch (_: Exception) {}
        }
    }
}
