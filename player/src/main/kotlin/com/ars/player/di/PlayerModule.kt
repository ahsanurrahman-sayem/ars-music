package com.ars.player.di

import com.ars.player.engine.ExoPlayerController
import com.ars.player.engine.PlayerController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindPlayerController(impl: ExoPlayerController): PlayerController
}
