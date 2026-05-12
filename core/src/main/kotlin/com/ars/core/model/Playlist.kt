package com.ars.core.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val trackIds: List<Long>,
    val createdAt: Long,
    val updatedAt: Long
) {
    val trackCount: Int get() = trackIds.size
}
