package com.ars.data.di

import com.ars.data.repository.ImportRepositoryImpl
import com.ars.domain.usecase.import.ImportUseCasePort
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindImportPort(impl: ImportRepositoryImpl): ImportUseCasePort
}
