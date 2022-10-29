package com.example.githubsearchrepository.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.githubsearchrepository.model.RemoteKeys
import com.example.githubsearchrepository.model.Repo

@Database(
    entities = [Repo::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
 abstract class RepoDatabase : RoomDatabase(){

    abstract fun reposDao(): RepoDao
    abstract fun remoteKeysDao(): RemoteKeysDao

}