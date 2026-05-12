package com.ars.domain.usecase.track

import com.ars.core.model.Track
import com.ars.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(): Flow<List<Track>> = repository.getAllTracks()
}

class GetTrackByIdUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(id: Long): Track? = repository.getTrackById(id)
}

class GetRecentlyPlayedUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(limit: Int = 20): Flow<List<Track>> = repository.getRecentlyPlayed(limit)
}

class GetFavoriteTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(): Flow<List<Track>> = repository.getFavoriteTracks()
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(trackId: Long) = repository.toggleFavorite(trackId)
}

class SearchTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(query: String): Flow<List<Track>> = repository.searchTracks(query)
}

class UpdatePlaybackStatsUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(trackId: Long) {
        repository.updateLastPlayed(trackId, System.currentTimeMillis())
        repository.incrementPlayCount(trackId)
    }
}
