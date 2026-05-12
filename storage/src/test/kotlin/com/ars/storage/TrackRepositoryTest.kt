package com.ars.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ars.core.model.Track
import com.ars.storage.db.ArSyncDatabase
import com.ars.storage.db.converter.toEntity
import com.ars.storage.repository.TrackRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TrackRepositoryTest {

    private lateinit var db: ArSyncDatabase
    private lateinit var repository: TrackRepositoryImpl

    private val testTrack = Track(
        id = 0,
        uri = "content://test/audio/1",
        title = "Test Track",
        artist = "Test Artist",
        album = "Test Album",
        albumArtist = "Test Artist",
        durationMs = 210_000L,
        fileSizeBytes = 5_000_000L,
        mimeType = "audio/mpeg",
        artworkUri = null,
        trackNumber = 1,
        year = 2024,
        bitrate = 320_000,
        sampleRate = 44100,
        isFavorite = false,
        dateAdded = System.currentTimeMillis(),
        lastPlayed = null,
        playCount = 0
    )

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ArSyncDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = TrackRepositoryImpl(db.trackDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndRetrieveTrack() = runTest {
        val id = repository.insertTrack(testTrack)
        assertTrue(id > 0)
        val retrieved = repository.getTrackById(id)
        assertNotNull(retrieved)
        assertEquals("Test Track", retrieved!!.title)
        assertEquals("Test Artist", retrieved.artist)
    }

    @Test
    fun getAllTracksReturnsInserted() = runTest {
        repository.insertTrack(testTrack)
        repository.insertTrack(testTrack.copy(uri = "content://test/audio/2", title = "Track 2"))
        val all = repository.getAllTracks().first()
        assertEquals(2, all.size)
    }

    @Test
    fun duplicateUriIsIgnored() = runTest {
        val id1 = repository.insertTrack(testTrack)
        val id2 = repository.insertTrack(testTrack) // same URI
        assertEquals(-1L, id2) // IGNORE strategy returns -1
        val all = repository.getAllTracks().first()
        assertEquals(1, all.size)
    }

    @Test
    fun toggleFavoriteFlips() = runTest {
        val id = repository.insertTrack(testTrack)
        repository.toggleFavorite(id)
        val track = repository.getTrackById(id)
        assertTrue(track!!.isFavorite)
        repository.toggleFavorite(id)
        val track2 = repository.getTrackById(id)
        assertFalse(track2!!.isFavorite)
    }

    @Test
    fun searchByTitle() = runTest {
        repository.insertTrack(testTrack)
        repository.insertTrack(testTrack.copy(uri = "content://test/2", title = "Jazz Night"))
        val results = repository.searchTracks("jazz").first()
        assertEquals(1, results.size)
        assertEquals("Jazz Night", results.first().title)
    }

    @Test
    fun getTrackByUri() = runTest {
        val id = repository.insertTrack(testTrack)
        val found = repository.getTrackByUri(testTrack.uri)
        assertNotNull(found)
        assertEquals(id, found!!.id)
    }

    @Test
    fun recentlyPlayedOrderedByTimestamp() = runTest {
        val id1 = repository.insertTrack(testTrack)
        val id2 = repository.insertTrack(testTrack.copy(uri = "content://test/2", title = "B"))
        repository.updateLastPlayed(id1, 1000L)
        repository.updateLastPlayed(id2, 2000L)
        val recent = repository.getRecentlyPlayed(10).first()
        assertEquals(id2, recent.first().id) // id2 is more recent
    }

    @Test
    fun incrementPlayCount() = runTest {
        val id = repository.insertTrack(testTrack)
        repository.incrementPlayCount(id)
        repository.incrementPlayCount(id)
        val track = repository.getTrackById(id)
        assertEquals(2, track!!.playCount)
    }

    @Test
    fun deleteTrack() = runTest {
        val id = repository.insertTrack(testTrack)
        repository.deleteTrack(id)
        assertNull(repository.getTrackById(id))
    }
}
