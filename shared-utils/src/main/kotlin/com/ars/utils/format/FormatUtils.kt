package com.ars.utils.format

import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Formats milliseconds → "M:SS" or "H:MM:SS"
 */
fun Long.formatDuration(): String {
    if (this <= 0L) return "0:00"
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}

/**
 * Formats bytes into human-readable size: "3.2 MB"
 */
fun Long.formatFileSize(): String {
    if (this <= 0L) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = this.toDouble()
    var unitIndex = 0
    while (size >= 1024 && unitIndex < units.lastIndex) {
        size /= 1024
        unitIndex++
    }
    return if (unitIndex == 0) "${size.toLong()} ${units[unitIndex]}"
    else String.format(Locale.US, "%.1f %s", size, units[unitIndex])
}
