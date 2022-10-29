package com.example.githubsearchrepository.data.source.local

import com.example.githubsearchrepository.data.source.local.db.RepoDatabase
import javax.inject.Inject


class LocalDataSource @Inject constructor(private val database: RepoDatabase): ILocalDataSource {


    override val databaseObject: RepoDatabase
        get() = database


}