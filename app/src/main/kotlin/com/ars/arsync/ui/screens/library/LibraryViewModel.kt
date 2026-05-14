package com.ars.arsync.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ars.core.model.Track
import com.ars.domain.usecase.track.GetAllTracksUseCase
import com.ars.domain.usecase.track.ToggleFavoriteUseCase
import com.ars.player.engine.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { TITLE, ARTIST, DATE_ADDED, DURATION }

data class LibraryUiState(
    val tracks: List<Track> = emptyList(),
    val filteredTracks: List<Track> = emptyList(),
    val currentTrackId: Long? = null,
    val isPlaying: Boolean = false,
    val sortOrder: SortOrder = SortOrder.TITLE
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAllTracksUseCase: GetAllTracksUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val playerController: PlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private var allTracks: List<Track> = emptyList()
    private var currentQuery: String = ""

    init {
        viewModelScope.launch {
            getAllTracksUseCase().collect { tracks ->
                allTracks = tracks
                applyFiltersAndSort()
            }
        }
        viewModelScope.launch {
            playerController.currentTrack.collect { track ->
                _uiState.update { it.copy(currentTrackId = track?.id) }
            }
        }
        viewModelScope.launch {
            playerController.isPlaying.collect { playing ->
                _uiState.update { it.copy(isPlaying = playing) }
            }
        }
    }

    fun search(query: String) {
        currentQuery = query
        applyFiltersAndSort()
    }

    fun cycleSortOrder() {
        val next = SortOrder.entries[((_uiState.value.sortOrder.ordinal + 1) % SortOrder.entries.size)]
        _uiState.update { it.copy(sortOrder = next) }
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        val filtered = if (currentQuery.isBlank()) allTracks
        else allTracks.filter { track ->
            track.title.contains(currentQuery, ignoreCase = true) ||
                    track.artist.contains(currentQuery, ignoreCase = true) ||
                    track.album.contains(currentQuery, ignoreCase = true)
        }
        val sorted = when (_uiState.value.sortOrder) {
            SortOrder.TITLE -> filtered.sortedBy { it.title }
            SortOrder.ARTIST -> filtered.sortedBy { it.artist }
            SortOrder.DATE_ADDED -> filtered.sortedByDescending { it.dateAdded }
            SortOrder.DURATION -> filtered.sortedByDescending { it.durationMs }
        }
        _uiState.update { it.copy(tracks = sorted, filteredTracks = sorted) }
    }

    fun playTrack(track: Track, queue: List<Track>) {
        viewModelScope.launch {
            playerController.setQueue(queue, queue.indexOfFirst { it.id == track.id })
            playerController.play()
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch { toggleFavoriteUseCase(track.id) }
    }
}
