package com.ars.core.model

/**
 * Core domain model for an audio track.
 * This is the single source of truth used across all modules.
 */
data class Track(
    val id: Long,
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String,
    val durationMs: Long,
    val fileSizeBytes: Long,
    val mimeType: String,
    val artworkUri: String?,
    val trackNumber: Int,
    val year: Int,
    val bitrate: Int,
    val sampleRate: Int,
    val isFavorite: Boolean,
    val dateAdded: Long,
    val lastPlayed: Long?,
    val playCount: Int
)
