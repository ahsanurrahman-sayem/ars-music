package com.ars.domain.usecase.import

import android.net.Uri
import com.ars.core.model.Track
import javax.inject.Inject

sealed class ImportResult {
    data class Success(val track: Track) : ImportResult()
    data class Duplicate(val existingTrack: Track) : ImportResult()
    data class Error(val throwable: Throwable) : ImportResult()
}

/**
 * Thin use-case wrapper. Delegates to ImportRepositoryImpl in :data.
 * We declare the dependency via the interface [ImportUseCase] so domain
 * stays free of data-layer concrete types at compile time.
 */
interface ImportUseCasePort {
    suspend fun importFromUri(uri: Uri): ImportResult
}

class ImportTrackUseCase @Inject constructor(
    private val importPort: ImportUseCasePort
) {
    suspend operator fun invoke(uri: Uri): ImportResult = try {
        importPort.importFromUri(uri)
    } catch (e: Exception) {
        ImportResult.Error(e)
    }
}
