package com.ars.domain.usecase.playlist

import com.ars.core.model.Playlist
import com.ars.core.model.Track
import com.ars.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPlaylistsUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    operator fun invoke(): Flow<List<Playlist>> = repository.getAllPlaylists()
}

class CreatePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(name: String, description: String = ""): Long =
        repository.createPlaylist(name, description)
}

class DeletePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long) = repository.deletePlaylist(playlistId)
}

class AddTrackToPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, trackId: Long) =
        repository.addTrackToPlaylist(playlistId, trackId)
}

class RemoveTrackFromPlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: Long, trackId: Long) =
        repository.removeTrackFromPlaylist(playlistId, trackId)
}

class GetPlaylistWithTracksUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    operator fun invoke(playlistId: Long): Flow<Pair<Playlist, List<Track>>> =
        repository.getPlaylistWithTracks(playlistId)
}
