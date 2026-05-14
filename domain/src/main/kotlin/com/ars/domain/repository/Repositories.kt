package com.ars.domain.repository

import com.ars.core.model.Playlist
import com.ars.core.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getAllTracks(): Flow<List<Track>>
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getRecentlyPlayed(limit: Int): Flow<List<Track>>
    suspend fun getTrackById(id: Long): Track?
    suspend fun getTrackByUri(uri: String): Track?
    suspend fun insertTrack(track: Track): Long
    suspend fun updateTrack(track: Track)
    suspend fun deleteTrack(id: Long)
    suspend fun toggleFavorite(id: Long)
    suspend fun updateLastPlayed(id: Long, timestamp: Long)
    suspend fun incrementPlayCount(id: Long)
    fun searchTracks(query: String): Flow<List<Track>>
}

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun createPlaylist(name: String, description: String): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(id: Long)
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    fun getPlaylistWithTracks(playlistId: Long): Flow<Pair<Playlist, List<Track>>>
}
