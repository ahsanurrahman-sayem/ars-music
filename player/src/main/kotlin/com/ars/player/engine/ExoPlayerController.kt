package com.ars.player.engine

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ars.core.model.RepeatMode
import com.ars.core.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ExoPlayer-backed [PlayerController].
 *
 * Runs ExoPlayer in the app process (not inside PlaybackService) but the
 * PlaybackService independently owns its own ExoPlayer instance for the
 * MediaSession / notification. The two are decoupled intentionally:
 *
 *  - ViewModels talk to ExoPlayerController via PlayerController interface.
 *  - PlaybackService is started by the OS notification / MediaSession machinery.
 *  - Playback state is authoritative inside PlaybackService; this controller
 *    mirrors it via position polling and Player.Listener.
 *
 * For a production app you would bridge them via MediaController connected to
 * the MediaSession. We keep this standalone version so the module compiles
 * without a circular dep on :background-service.
 */
@Singleton
class ExoPlayerController @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerController {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus= */ true
            )
            .setHandleAudioBecomingNoisy(true)   // auto-pause on headset unplug
            .setWakeMode(C.WAKE_MODE_LOCAL)       // keep CPU on during playback
            .build()
            .also { player ->
                player.addListener(playerListener)
                scope.launch { pollPosition(player) }
            }
    }

    private val _currentTrack     = MutableStateFlow<Track?>(null)
    private val _isPlaying        = MutableStateFlow(false)
    private val _position         = MutableStateFlow(0L)
    private val _duration         = MutableStateFlow(0L)
    private val _shuffleEnabled   = MutableStateFlow(false)
    private val _repeatMode       = MutableStateFlow(RepeatMode.OFF)
    private val _playerVolume     = MutableStateFlow(1f)
    private val _queue            = MutableStateFlow<List<Track>>(emptyList())
    private val _currentQueueIndex = MutableStateFlow(-1)

    override val currentTrack:      StateFlow<Track?>       = _currentTrack.asStateFlow()
    override val isPlaying:         StateFlow<Boolean>      = _isPlaying.asStateFlow()
    override val position:          StateFlow<Long>         = _position.asStateFlow()
    override val duration:          StateFlow<Long>         = _duration.asStateFlow()
    override val shuffleEnabled:    StateFlow<Boolean>      = _shuffleEnabled.asStateFlow()
    override val repeatMode:        StateFlow<RepeatMode>   = _repeatMode.asStateFlow()
    override val playerVolume:      StateFlow<Float>        = _playerVolume.asStateFlow()
    override val queue:             StateFlow<List<Track>>  = _queue.asStateFlow()
    override val currentQueueIndex: StateFlow<Int>          = _currentQueueIndex.asStateFlow()

    // ── Listener ─────────────────────────────────────────────────────────────

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val index = exoPlayer.currentMediaItemIndex
            _currentQueueIndex.value = index
            _currentTrack.value = _queue.value.getOrNull(index)
            _duration.value = exoPlayer.duration.coerceAtLeast(0L)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _duration.value = exoPlayer.duration.coerceAtLeast(0L)
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _shuffleEnabled.value = shuffleModeEnabled
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _repeatMode.value = when (repeatMode) {
                Player.REPEAT_MODE_OFF -> RepeatMode.OFF
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                else                   -> RepeatMode.OFF
            }
        }
    }

    // ── Position polling (500 ms tick) ───────────────────────────────────────

    private suspend fun pollPosition(player: ExoPlayer) {
        while (scope.isActive) {
            _position.value = player.currentPosition.coerceAtLeast(0L)
            delay(500L)
        }
    }

    // ── PlayerController API ─────────────────────────────────────────────────

    override suspend fun setQueue(tracks: List<Track>, startIndex: Int) =
        withContext(Dispatchers.Main) {
            _queue.value = tracks
            val mediaItems = tracks.map { track ->
                MediaItem.Builder()
                    .setUri(Uri.parse(track.uri))
                    .setMediaId(track.id.toString())
                    .build()
            }
            exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
            exoPlayer.prepare()
            _currentTrack.value      = tracks.getOrNull(startIndex)
            _currentQueueIndex.value = startIndex
        }

    override suspend fun play() = withContext(Dispatchers.Main) {
        exoPlayer.play()
    }

    override suspend fun pause() = withContext(Dispatchers.Main) {
        exoPlayer.pause()
    }

    override suspend fun togglePlayPause() = withContext(Dispatchers.Main) {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    override suspend fun next() = withContext(Dispatchers.Main) {
        if (exoPlayer.hasNextMediaItem()) exoPlayer.seekToNextMediaItem()
    }

    override suspend fun previous() = withContext(Dispatchers.Main) {
        when {
            exoPlayer.currentPosition > 3000L   -> exoPlayer.seekTo(0L)
            exoPlayer.hasPreviousMediaItem()     -> exoPlayer.seekToPreviousMediaItem()
            else                                 -> exoPlayer.seekTo(0L)
        }
    }

    override suspend fun seekTo(positionMs: Long) = withContext(Dispatchers.Main) {
        exoPlayer.seekTo(positionMs)
    }

    override suspend fun toggleShuffle() = withContext(Dispatchers.Main) {
        exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
    }

    override suspend fun cycleRepeatMode() = withContext(Dispatchers.Main) {
        exoPlayer.repeatMode = when (exoPlayer.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else                   -> Player.REPEAT_MODE_OFF
        }
    }

    override suspend fun setVolume(volume: Float) = withContext(Dispatchers.Main) {
        val clamped = volume.coerceIn(0f, 1f)
        exoPlayer.volume    = clamped
        _playerVolume.value = clamped
    }

    override suspend fun stop() = withContext(Dispatchers.Main) {
        exoPlayer.stop()
        _isPlaying.value = false
    }
}
