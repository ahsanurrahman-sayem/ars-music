package com.ars.utils.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Centralised dispatcher container. Inject this instead of referencing
 * Dispatchers.* directly so tests can substitute a TestDispatcher.
 */
data class AppDispatchers(
    val main: CoroutineDispatcher = Dispatchers.Main,
    val io: CoroutineDispatcher = Dispatchers.IO,
    val default: CoroutineDispatcher = Dispatchers.Default
)
