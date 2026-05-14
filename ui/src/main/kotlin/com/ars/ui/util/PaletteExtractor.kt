package com.ars.ui.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

/**
 * Extracts a dark-friendly gradient palette from album artwork.
 * Returns a list of two Compose Colors suitable for a vertical gradient background.
 */
object PaletteExtractor {

    fun extractGradientColors(bitmap: Bitmap): List<Color> {
        val palette = Palette.from(bitmap)
            .maximumColorCount(8)
            .generate()

        val dominant = palette.getDominantColor(0xFF1A1A2E.toInt())
        val muted = palette.getDarkMutedColor(
            palette.getMutedColor(0xFF0D0D1A.toInt())
        )

        val top = darken(dominant, factor = 0.6f)
        val bottom = darken(muted, factor = 0.4f)

        return listOf(Color(top), Color(bottom))
    }

    private fun darken(color: Int, factor: Float): Int {
        val a = (color shr 24) and 0xFF
        val r = ((color shr 16) and 0xFF) * factor
        val g = ((color shr 8) and 0xFF) * factor
        val b = (color and 0xFF) * factor
        return (a shl 24) or
                (r.toInt().coerceIn(0, 255) shl 16) or
                (g.toInt().coerceIn(0, 255) shl 8) or
                b.toInt().coerceIn(0, 255)
    }
}
