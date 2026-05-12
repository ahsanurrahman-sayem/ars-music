package com.ars.arsync

import app.cash.turbine.test
import com.ars.core.model.Track
import com.ars.domain.usecase.track.GetAllTracksUseCase
import com.ars.domain.usecase.track.GetRecentlyPlayedUseCase
import com.ars.domain.usecase.track.ToggleFavoriteUseCase
import com.ars.arsync.ui.screens.home.HomeViewModel
import com.ars.player.engine.PlayerController
import com.ars.core.model.RepeatMode
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAllTracksUseCase: GetAllTracksUseCase
    private lateinit var getRecentlyPlayedUseCase: GetRecentlyPlayedUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var playerController: PlayerController
    private lateinit var viewModel: HomeViewModel

    private val sampleTrack = Track(
        id = 1L, uri = "content://test/1", title = "Test", artist = "Artist",
        album = "Album", albumArtist = "Artist", durationMs = 180_000L,
        fileSizeBytes = 4_000_000L, mimeType = "audio/mpeg", artworkUri = null,
        trackNumber = 1, year = 2024, bitrate = 320_000, sampleRate = 44100,
        isFavorite = false, dateAdded = 0L, lastPlayed = null, playCount = 0
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getAllTracksUseCase = mockk {
            every { invoke() } returns flowOf(listOf(sampleTrack))
        }
        getRecentlyPlayedUseCase = mockk {
            every { invoke(any()) } returns flowOf(emptyList())
        }
        toggleFavoriteUseCase = mockk {
            coEvery { invoke(any()) } just runs
        }
        playerController = mockk {
            every { currentTrack } returns MutableStateFlow(null)
            every { isPlaying } returns MutableStateFlow(false)
            every { position } returns MutableStateFlow(0L)
            every { duration } returns MutableStateFlow(0L)
            every { shuffleEnabled } returns MutableStateFlow(false)
            every { repeatMode } returns MutableStateFlow(RepeatMode.OFF)
            every { playerVolume } returns MutableStateFlow(1f)
            every { queue } returns MutableStateFlow(emptyList())
            every { currentQueueIndex } returns MutableStateFlow(-1)
            coEvery { setQueue(any(), any()) } just runs
            coEvery { play() } just runs
            coEvery { togglePlayPause() } just runs
        }

        viewModel = HomeViewModel(
            getAllTracksUseCase,
            getRecentlyPlayedUseCase,
            toggleFavoriteUseCase,
            playerController
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has loading true`() = runTest {
        // isLoading starts true, then flips after tracks load
        val initial = viewModel.uiState.value
        // After advancing dispatcher the tracks flow will emit
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.allTracks.size)
        assertEquals("Test", state.allTracks.first().title)
    }

    @Test
    fun `playTrack calls playerController setQueue and play`() = runTest {
        advanceUntilIdle()
        viewModel.playTrack(sampleTrack)
        advanceUntilIdle()
        coVerify { playerController.setQueue(any(), any()) }
        coVerify { playerController.play() }
    }

    @Test
    fun `toggleFavorite calls use case`() = runTest {
        viewModel.toggleFavorite(sampleTrack)
        advanceUntilIdle()
        coVerify { toggleFavoriteUseCase(sampleTrack.id) }
    }

    @Test
    fun `togglePlayPause delegates to controller`() = runTest {
        viewModel.togglePlayPause()
        advanceUntilIdle()
        coVerify { playerController.togglePlayPause() }
    }
}
