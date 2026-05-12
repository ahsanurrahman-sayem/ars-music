package com.ars.player.engine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepTimer @Inject constructor(
    private val playerController: PlayerController
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null

    private val _remainingMs = MutableStateFlow<Long?>(null)
    val remainingMs: StateFlow<Long?> = _remainingMs.asStateFlow()

    fun start(durationMs: Long) {
        timerJob?.cancel()
        val endTime = System.currentTimeMillis() + durationMs
        Timber.d("Sleep timer set for ${durationMs / 1000}s")

        timerJob = scope.launch {
            while (isActive) {
                val remaining = endTime - System.currentTimeMillis()
                if (remaining <= 0L) {
                    _remainingMs.value = null
                    playerController.pause()
                    Timber.d("Sleep timer fired — playback paused")
                    break
                }
                _remainingMs.value = remaining
                delay(1000L)
            }
        }
    }

    fun cancel() {
        timerJob?.cancel()
        timerJob = null
        _remainingMs.value = null
        Timber.d("Sleep timer cancelled")
    }
}
