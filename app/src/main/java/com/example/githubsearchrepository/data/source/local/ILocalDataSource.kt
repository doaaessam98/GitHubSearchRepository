package com.example.githubsearchrepository.data.source.local

import com.example.githubsearchrepository.data.source.local.db.RepoDatabase


interface ILocalDataSource {
    val databaseObject: RepoDatabase
}