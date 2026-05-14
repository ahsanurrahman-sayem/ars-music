package com.ars.domain

import android.net.Uri
import com.ars.core.model.Track
import com.ars.domain.usecase.import.ImportResult
import com.ars.domain.usecase.import.ImportTrackUseCase
import com.ars.domain.usecase.import.ImportUseCasePort
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ImportUseCaseTest {

    private val port: ImportUseCasePort = mockk()
    private val useCase = ImportTrackUseCase(port)

    private val fakeUri: Uri = mockk {
        every { toString() } returns "content://test/audio/1"
    }

    private val fakeTrack = Track(
        id = 1L, uri = "content://test/audio/1", title = "Song",
        artist = "Artist", album = "Album", albumArtist = "Artist",
        durationMs = 200_000L, fileSizeBytes = 5_000_000L, mimeType = "audio/mpeg",
        artworkUri = null, trackNumber = 1, year = 2024, bitrate = 320_000,
        sampleRate = 44100, isFavorite = false, dateAdded = 0L, lastPlayed = null, playCount = 0
    )

    @Test
    fun `successful import returns Success`() = runTest {
        coEvery { port.importFromUri(fakeUri) } returns ImportResult.Success(fakeTrack)
        val result = useCase(fakeUri)
        assertTrue(result is ImportResult.Success)
        assertEquals(fakeTrack.id, (result as ImportResult.Success).track.id)
    }

    @Test
    fun `duplicate import returns Duplicate`() = runTest {
        coEvery { port.importFromUri(fakeUri) } returns ImportResult.Duplicate(fakeTrack)
        val result = useCase(fakeUri)
        assertTrue(result is ImportResult.Duplicate)
    }

    @Test
    fun `exception in port returns Error`() = runTest {
        coEvery { port.importFromUri(fakeUri) } throws RuntimeException("IO failure")
        val result = useCase(fakeUri)
        assertTrue(result is ImportResult.Error)
        assertTrue((result as ImportResult.Error).throwable.message?.contains("IO failure") == true)
    }
}
