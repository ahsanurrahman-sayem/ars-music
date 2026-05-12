package com.ars.utils

import com.ars.utils.format.formatDuration
import com.ars.utils.format.formatFileSize
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun `formatDuration zero returns 0-00`() {
        assertEquals("0:00", 0L.formatDuration())
    }

    @Test
    fun `formatDuration seconds only`() {
        assertEquals("0:45", 45_000L.formatDuration())
    }

    @Test
    fun `formatDuration minutes and seconds`() {
        assertEquals("3:30", (3 * 60_000L + 30_000L).formatDuration())
    }

    @Test
    fun `formatDuration hours included`() {
        assertEquals("1:02:03", (3600_000L + 2 * 60_000L + 3_000L).formatDuration())
    }

    @Test
    fun `formatFileSize bytes`() {
        assertEquals("512 B", 512L.formatFileSize())
    }

    @Test
    fun `formatFileSize kilobytes`() {
        assertEquals("1.0 KB", 1024L.formatFileSize())
    }

    @Test
    fun `formatFileSize megabytes`() {
        assertEquals("3.8 MB", (4_000_000L).formatFileSize())
    }

    @Test
    fun `formatDuration negative returns 0-00`() {
        assertEquals("0:00", (-1L).formatDuration())
    }
}
