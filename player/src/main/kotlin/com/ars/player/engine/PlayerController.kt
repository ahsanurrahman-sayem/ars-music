package com.ars.player.engine

import com.ars.core.model.RepeatMode
import com.ars.core.model.Track
import kotlinx.coroutines.flow.StateFlow

/**
 * Single entry-point for all playback control.
 * ViewModels depend only on this interface; ExoPlayerController is the impl.
 */
interface PlayerController {
    val currentTrack:       StateFlow<Track?>
    val isPlaying:          StateFlow<Boolean>
    val position:           StateFlow<Long>
    val duration:           StateFlow<Long>
    val shuffleEnabled:     StateFlow<Boolean>
    val repeatMode:         StateFlow<RepeatMode>
    val playerVolume:       StateFlow<Float>
    val queue:              StateFlow<List<Track>>
    val currentQueueIndex:  StateFlow<Int>

    suspend fun setQueue(tracks: List<Track>, startIndex: Int)
    suspend fun play()
    suspend fun pause()
    suspend fun togglePlayPause()
    suspend fun next()
    suspend fun previous()
    suspend fun seekTo(positionMs: Long)
    suspend fun toggleShuffle()
    suspend fun cycleRepeatMode()
    suspend fun setVolume(volume: Float)
    suspend fun stop()
}
