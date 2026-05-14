package com.ars.core.model

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class PlaybackState(
    val currentTrack: Track?,
    val isPlaying: Boolean,
    val position: Long,
    val duration: Long,
    val shuffleEnabled: Boolean,
    val repeatMode: RepeatMode,
    val queue: List<Track>,
    val currentQueueIndex: Int
) {
    companion object {
        val EMPTY = PlaybackState(
            currentTrack = null,
            isPlaying = false,
            position = 0L,
            duration = 0L,
            shuffleEnabled = false,
            repeatMode = RepeatMode.OFF,
            queue = emptyList(),
            currentQueueIndex = -1
        )
    }
}

data class EqBand(
    val centerFrequency: Int,   // Hz
    val gainDb: Float            // -15.0 to +15.0 dB
)

data class EqualizerPreset(
    val name: String,
    val bands: List<EqBand>
)
