package com.ars.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ars.storage.db.dao.PlaylistDao
import com.ars.storage.db.dao.TrackDao
import com.ars.storage.db.entity.PlaylistEntity
import com.ars.storage.db.entity.PlaylistTrackCrossRef
import com.ars.storage.db.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ArSyncDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        const val DATABASE_NAME = "arsync.db"
    }
}
