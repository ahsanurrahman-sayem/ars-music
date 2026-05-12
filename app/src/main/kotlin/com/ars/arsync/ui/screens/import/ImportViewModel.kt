package com.ars.arsync.ui.screens.import

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ars.domain.usecase.import.ImportResult
import com.ars.domain.usecase.import.ImportTrackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ImportUiState(
    val isImporting: Boolean = false,
    val importedTrackId: Long? = null,
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val importTrackUseCase: ImportTrackUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    fun importUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, errorMessage = null, statusMessage = "Reading file...") }
            when (val result = importTrackUseCase(uri)) {
                is ImportResult.Success -> {
                    _uiState.update { it.copy(
                        isImporting = false,
                        importedTrackId = result.track.id,
                        statusMessage = "Imported: ${result.track.title}"
                    ) }
                }
                is ImportResult.Duplicate -> {
                    _uiState.update { it.copy(
                        isImporting = false,
                        importedTrackId = result.existingTrack.id,
                        statusMessage = "Already imported: ${result.existingTrack.title}"
                    ) }
                }
                is ImportResult.Error -> {
                    _uiState.update { it.copy(
                        isImporting = false,
                        errorMessage = "Import failed: ${result.throwable.message}"
                    ) }
                }
            }
        }
    }
}
