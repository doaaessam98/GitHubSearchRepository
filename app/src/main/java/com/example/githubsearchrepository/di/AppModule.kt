package com.example.githubsearchrepository.di

import com.example.githubsearchrepository.data.repository.IRepository
import com.example.githubsearchrepository.data.repository.Repository
import com.example.githubsearchrepository.data.source.local.ILocalDataSource
import com.example.githubsearchrepository.data.source.local.LocalDataSource
import com.example.githubsearchrepository.data.source.remote.IRemoteDataSource
import com.example.githubsearchrepository.data.source.remote.api.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface AppModule {

    @Binds
    fun provideLocalDataSource(localDataSource: LocalDataSource): ILocalDataSource

    @Binds
    fun provideRemoteDataSource(remoteDataSource: RemoteDataSource): IRemoteDataSource

    @Binds
    fun provideRepository(repository: Repository): IRepository

}