package com.ars.arsync.ui.screens.player

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ars.core.model.RepeatMode
import com.ars.core.model.Track
import com.ars.domain.usecase.track.GetTrackByIdUseCase
import com.ars.domain.usecase.track.ToggleFavoriteUseCase
import com.ars.player.engine.PlayerController
import com.ars.player.engine.SleepTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isFavorite: Boolean = false,
    val playerVolume: Float = 1f,
    val dominantColors: List<Color>? = null,
    val sleepTimerRemaining: Long? = null,
    val showSleepTimerDialog: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val getTrackByIdUseCase: GetTrackByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val sleepTimer: SleepTimer
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        observePlayer()
        observeSleepTimer()
    }

    private fun observePlayer() {
        viewModelScope.launch {
            playerController.currentTrack.collect { track ->
                _uiState.update { it.copy(currentTrack = track, isFavorite = track?.isFavorite ?: false) }
            }
        }
        viewModelScope.launch {
            playerController.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlaying = playing) }
            }
        }
        viewModelScope.launch {
            playerController.position.collect { pos ->
                _uiState.update { it.copy(position = pos) }
            }
        }
        viewModelScope.launch {
            playerController.duration.collect { dur ->
                _uiState.update { it.copy(duration = dur) }
            }
        }
        viewModelScope.launch {
            playerController.shuffleEnabled.collect { shuffle ->
                _uiState.update { it.copy(shuffleEnabled = shuffle) }
            }
        }
        viewModelScope.launch {
            playerController.repeatMode.collect { repeat ->
                _uiState.update { it.copy(repeatMode = repeat) }
            }
        }
        viewModelScope.launch {
            playerController.playerVolume.collect { vol ->
                _uiState.update { it.copy(playerVolume = vol) }
            }
        }
    }

    private fun observeSleepTimer() {
        viewModelScope.launch {
            sleepTimer.remainingMs.collect { remaining ->
                _uiState.update { it.copy(sleepTimerRemaining = remaining) }
            }
        }
    }

    fun ensureTrackLoaded(trackId: Long) {
        viewModelScope.launch {
            val current = _uiState.value.currentTrack
            if (current?.id != trackId) {
                getTrackByIdUseCase(trackId)?.let { track ->
                    playerController.setQueue(listOf(track), 0)
                    playerController.play()
                }
            }
        }
    }

    fun togglePlayPause() = viewModelScope.launch { playerController.togglePlayPause() }
    fun previous() = viewModelScope.launch { playerController.previous() }
    fun next() = viewModelScope.launch { playerController.next() }
    fun seekTo(position: Long) = viewModelScope.launch { playerController.seekTo(position) }
    fun toggleShuffle() = viewModelScope.launch { playerController.toggleShuffle() }
    fun cycleRepeatMode() = viewModelScope.launch { playerController.cycleRepeatMode() }

    fun setPlayerVolume(volume: Float) {
        viewModelScope.launch { playerController.setVolume(volume) }
    }

    fun toggleFavorite() {
        val trackId = _uiState.value.currentTrack?.id ?: return
        viewModelScope.launch { toggleFavoriteUseCase(trackId) }
    }

    fun showSleepTimerDialog() {
        _uiState.update { it.copy(showSleepTimerDialog = true) }
    }

    fun dismissSleepTimerDialog() {
        _uiState.update { it.copy(showSleepTimerDialog = false) }
    }

    fun setSleepTimer(durationMs: Long) {
        if (durationMs <= 0L) {
            sleepTimer.cancel()
        } else {
            sleepTimer.start(durationMs)
        }
    }
}
