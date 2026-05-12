package com.ars.arsync.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ars.arsync.ui.navigation.NavEvent
import com.ars.domain.usecase.import.ImportTrackUseCase
import com.ars.domain.usecase.import.ImportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class MainUiState(
    val dynamicColorEnabled: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val importTrackUseCase: ImportTrackUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<NavEvent>(replay = 1)
    val navEvent: SharedFlow<NavEvent> = _navEvent.asSharedFlow()

    fun handleImportUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = importTrackUseCase(uri)) {
                is ImportResult.Success -> {
                    Timber.d("Imported track: ${result.track.title}")
                    _navEvent.emit(NavEvent.NavigateToPlayer(result.track.id))
                }
                is ImportResult.Duplicate -> {
                    Timber.d("Duplicate track: ${result.existingTrack.title}")
                    _navEvent.emit(NavEvent.NavigateToPlayer(result.existingTrack.id))
                }
                is ImportResult.Error -> {
                    Timber.e(result.throwable, "Import failed")
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun clearNavEvent() {
        // consumed by nav host
    }
}
