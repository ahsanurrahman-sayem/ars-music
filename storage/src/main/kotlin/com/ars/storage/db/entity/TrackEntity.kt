package com.ars.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["uri"], unique = true),
        Index(value = ["is_favorite"]),
        Index(value = ["last_played"]),
        Index(value = ["title"]),
        Index(value = ["artist"])
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "uri")
    val uri: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "artist")
    val artist: String,

    @ColumnInfo(name = "album")
    val album: String,

    @ColumnInfo(name = "album_artist")
    val albumArtist: String,

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long,

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long,

    @ColumnInfo(name = "mime_type")
    val mimeType: String,

    @ColumnInfo(name = "artwork_uri")
    val artworkUri: String?,

    @ColumnInfo(name = "track_number")
    val trackNumber: Int,

    @ColumnInfo(name = "year")
    val year: Int,

    @ColumnInfo(name = "bitrate")
    val bitrate: Int,

    @ColumnInfo(name = "sample_rate")
    val sampleRate: Int,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "date_added")
    val dateAdded: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_played")
    val lastPlayed: Long? = null,

    @ColumnInfo(name = "play_count")
    val playCount: Int = 0
)
