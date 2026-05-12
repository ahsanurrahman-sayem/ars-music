package com.ars.arsync.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ars.storage.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val respectAudioFocus: Boolean = true,
    val duckingEnabled: Boolean = true,
    val pauseOnHeadsetDisconnect: Boolean = true,
    val dynamicColorEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.respectAudioFocus,
        settingsRepository.duckingEnabled,
        settingsRepository.pauseOnHeadsetDisconnect,
        settingsRepository.dynamicColorEnabled
    ) { audioFocus, ducking, headset, dynamic ->
        SettingsUiState(
            respectAudioFocus = audioFocus,
            duckingEnabled = ducking,
            pauseOnHeadsetDisconnect = headset,
            dynamicColorEnabled = dynamic
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setRespectAudioFocus(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.setRespectAudioFocus(enabled) }

    fun setDuckingEnabled(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.setDuckingEnabled(enabled) }

    fun setPauseOnHeadsetDisconnect(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.setPauseOnHeadsetDisconnect(enabled) }

    fun setDynamicColorEnabled(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.setDynamicColorEnabled(enabled) }
}
