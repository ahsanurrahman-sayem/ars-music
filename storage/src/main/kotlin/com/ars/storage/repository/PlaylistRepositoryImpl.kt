package com.ars.storage.repository

import com.ars.core.model.Playlist
import com.ars.core.model.Track
import com.ars.domain.repository.PlaylistRepository
import com.ars.storage.db.converter.toDomain
import com.ars.storage.db.dao.PlaylistDao
import com.ars.storage.db.entity.PlaylistEntity
import com.ars.storage.db.entity.PlaylistTrackCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    trackIds = emptyList(), // loaded separately for performance
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }

    override suspend fun getPlaylistById(id: Long): Playlist? =
        playlistDao.getPlaylistById(id)?.let { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                trackIds = emptyList(),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }

    override suspend fun createPlaylist(name: String, description: String): Long =
        playlistDao.insertPlaylist(
            PlaylistEntity(name = name, description = description)
        )

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(
            PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                createdAt = playlist.createdAt,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deletePlaylist(id: Long) = playlistDao.deletePlaylist(id)

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val maxPos = playlistDao.getMaxPosition(playlistId) ?: -1
        playlistDao.addTrackToPlaylist(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = trackId,
                position = maxPos + 1
            )
        )
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistDao.removeTrackFromPlaylist(
            PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId)
        )
    }

    override fun getPlaylistWithTracks(playlistId: Long): Flow<Pair<Playlist, List<Track>>> =
        playlistDao.getPlaylistWithTracks(playlistId)
            .filterNotNull()
            .map { playlistWithTracks ->
                val playlist = Playlist(
                    id = playlistWithTracks.playlist.id,
                    name = playlistWithTracks.playlist.name,
                    description = playlistWithTracks.playlist.description,
                    trackIds = playlistWithTracks.tracks.map { it.id },
                    createdAt = playlistWithTracks.playlist.createdAt,
                    updatedAt = playlistWithTracks.playlist.updatedAt
                )
                val tracks = playlistWithTracks.tracks.map { it.toDomain() }
                Pair(playlist, tracks)
            }
}
