package com.ars.background.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.ars.domain.usecase.track.UpdatePlaybackStatsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var updatePlaybackStatsUseCase: UpdatePlaybackStatsUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        Timber.d("PlaybackService created")
        initPlayer()
        initMediaSession()
    }

    private fun initPlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
            .setHandleAudioBecomingNoisy(true) // auto-pause on headset unplug
            .setWakeMode(C.WAKE_MODE_LOCAL)    // prevent CPU sleep during playback
            .build()

        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // Update play stats when track actually changes
                mediaItem?.mediaId?.toLongOrNull()?.let { trackId ->
                    serviceScope.launch {
                        updatePlaybackStatsUseCase(trackId)
                    }
                }
            }
        })
    }

    private fun initMediaSession() {
        // Deep link back into the app when notification is tapped
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Stop service when app is swiped from recents ONLY if not playing
        if (!player.isPlaying) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        Timber.d("PlaybackService destroyed")
        mediaSession.run {
            player.release()
            release()
        }
        super.onDestroy()
    }
}
