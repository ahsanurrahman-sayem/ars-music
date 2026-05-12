package com.ars.storage.repository

import com.ars.core.model.Track
import com.ars.domain.repository.TrackRepository
import com.ars.storage.db.converter.toDomain
import com.ars.storage.db.converter.toEntity
import com.ars.storage.db.dao.TrackDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao
) : TrackRepository {

    override fun getAllTracks(): Flow<List<Track>> =
        trackDao.getAllTracks().map { entities -> entities.map { it.toDomain() } }

    override fun getFavoriteTracks(): Flow<List<Track>> =
        trackDao.getFavoriteTracks().map { entities -> entities.map { it.toDomain() } }

    override fun getRecentlyPlayed(limit: Int): Flow<List<Track>> =
        trackDao.getRecentlyPlayed(limit).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTrackById(id: Long): Track? =
        trackDao.getTrackById(id)?.toDomain()

    override suspend fun getTrackByUri(uri: String): Track? =
        trackDao.getTrackByUri(uri)?.toDomain()

    override suspend fun insertTrack(track: Track): Long =
        trackDao.insertTrack(track.toEntity())

    override suspend fun updateTrack(track: Track) =
        trackDao.updateTrack(track.toEntity())

    override suspend fun deleteTrack(id: Long) =
        trackDao.deleteTrack(id)

    override suspend fun toggleFavorite(id: Long) =
        trackDao.toggleFavorite(id)

    override suspend fun updateLastPlayed(id: Long, timestamp: Long) =
        trackDao.updateLastPlayed(id, timestamp)

    override suspend fun incrementPlayCount(id: Long) =
        trackDao.incrementPlayCount(id)

    override fun searchTracks(query: String): Flow<List<Track>> =
        trackDao.searchTracks(query).map { entities -> entities.map { it.toDomain() } }
}
