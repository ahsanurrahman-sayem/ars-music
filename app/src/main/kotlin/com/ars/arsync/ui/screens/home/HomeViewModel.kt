package com.ars.arsync.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ars.core.model.Track
import com.ars.domain.usecase.track.GetAllTracksUseCase
import com.ars.domain.usecase.track.GetRecentlyPlayedUseCase
import com.ars.domain.usecase.track.ToggleFavoriteUseCase
import com.ars.player.engine.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val allTracks: List<Track> = emptyList(),
    val recentTracks: List<Track> = emptyList(),
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllTracksUseCase: GetAllTracksUseCase,
    private val getRecentlyPlayedUseCase: GetRecentlyPlayedUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val playerController: PlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTracks()
        observePlayer()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            combine(
                getAllTracksUseCase(),
                getRecentlyPlayedUseCase(limit = 10)
            ) { all, recent ->
                _uiState.update { state ->
                    state.copy(
                        allTracks = all,
                        recentTracks = recent,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    private fun observePlayer() {
        viewModelScope.launch {
            playerController.currentTrack.collect { track ->
                _uiState.update { it.copy(currentTrack = track) }
            }
        }
        viewModelScope.launch {
            playerController.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlaying = playing) }
            }
        }
    }

    fun playTrack(track: Track) {
        viewModelScope.launch {
            val queue = _uiState.value.allTracks
            playerController.setQueue(queue, queue.indexOfFirst { it.id == track.id })
            playerController.play()
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            playerController.togglePlayPause()
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            toggleFavoriteUseCase(track.id)
        }
    }
}
