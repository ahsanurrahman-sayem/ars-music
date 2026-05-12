package com.ars.storage.di

import android.content.Context
import androidx.room.Room
import com.ars.domain.repository.PlaylistRepository
import com.ars.domain.repository.TrackRepository
import com.ars.storage.db.ArSyncDatabase
import com.ars.storage.db.dao.PlaylistDao
import com.ars.storage.db.dao.TrackDao
import com.ars.storage.repository.PlaylistRepositoryImpl
import com.ars.storage.repository.TrackRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ArSyncDatabase =
        Room.databaseBuilder(context, ArSyncDatabase::class.java, ArSyncDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTrackDao(db: ArSyncDatabase): TrackDao = db.trackDao()

    @Provides
    fun providePlaylistDao(db: ArSyncDatabase): PlaylistDao = db.playlistDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrackRepository(impl: TrackRepositoryImpl): TrackRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository
}
