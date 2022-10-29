package com.example.githubsearchrepository.di

import android.content.Context
import androidx.room.Room
import com.example.githubsearchrepository.data.source.local.db.RemoteKeysDao
import com.example.githubsearchrepository.data.source.local.db.RepoDao
import com.example.githubsearchrepository.data.source.local.db.RepoDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataBaseModule {



     @Provides
     @Singleton
     fun repoDataBase(@ApplicationContext context: Context): RepoDatabase =
         Room.databaseBuilder(context,RepoDatabase::class.java,"repo_DB").build()

    @Provides
    @Singleton
    fun provideRepoDataBase(db:RepoDatabase): RepoDao =db.reposDao()

    @Provides
    @Singleton
    fun provideRemoteKeysDataBase(db:RepoDatabase): RemoteKeysDao =db.remoteKeysDao()
}


