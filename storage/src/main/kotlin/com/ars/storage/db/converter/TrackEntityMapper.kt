package com.ars.storage.db.converter

import com.ars.core.model.Track
import com.ars.storage.db.entity.TrackEntity

fun TrackEntity.toDomain(): Track = Track(
    id = id,
    uri = uri,
    title = title,
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    durationMs = durationMs,
    fileSizeBytes = fileSizeBytes,
    mimeType = mimeType,
    artworkUri = artworkUri,
    trackNumber = trackNumber,
    year = year,
    bitrate = bitrate,
    sampleRate = sampleRate,
    isFavorite = isFavorite,
    dateAdded = dateAdded,
    lastPlayed = lastPlayed,
    playCount = playCount
)

fun Track.toEntity(): TrackEntity = TrackEntity(
    id = id,
    uri = uri,
    title = title,
    artist = artist,
    album = album,
    albumArtist = albumArtist,
    durationMs = durationMs,
    fileSizeBytes = fileSizeBytes,
    mimeType = mimeType,
    artworkUri = artworkUri,
    trackNumber = trackNumber,
    year = year,
    bitrate = bitrate,
    sampleRate = sampleRate,
    isFavorite = isFavorite,
    dateAdded = dateAdded,
    lastPlayed = lastPlayed,
    playCount = playCount
)
