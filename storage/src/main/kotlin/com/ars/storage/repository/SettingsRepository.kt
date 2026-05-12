package com.ars.storage.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "arsync_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val RESPECT_AUDIO_FOCUS = booleanPreferencesKey("respect_audio_focus")
        val DUCKING_ENABLED = booleanPreferencesKey("ducking_enabled")
        val PAUSE_ON_HEADSET_DISCONNECT = booleanPreferencesKey("pause_on_headset_disconnect")
        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
    }

    val respectAudioFocus: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.RESPECT_AUDIO_FOCUS] ?: true }

    val duckingEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.DUCKING_ENABLED] ?: true }

    val pauseOnHeadsetDisconnect: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.PAUSE_ON_HEADSET_DISCONNECT] ?: true }

    val dynamicColorEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[Keys.DYNAMIC_COLOR_ENABLED] ?: true }

    suspend fun setRespectAudioFocus(enabled: Boolean) {
        context.dataStore.edit { it[Keys.RESPECT_AUDIO_FOCUS] = enabled }
    }

    suspend fun setDuckingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DUCKING_ENABLED] = enabled }
    }

    suspend fun setPauseOnHeadsetDisconnect(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PAUSE_ON_HEADSET_DISCONNECT] = enabled }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR_ENABLED] = enabled }
    }
}
