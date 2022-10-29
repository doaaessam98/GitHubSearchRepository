package com.example.githubsearchrepository.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubsearchrepository.data.paging.GithubPagingSource
import com.example.githubsearchrepository.data.paging.GithubRemoteMediator
import com.example.githubsearchrepository.data.source.local.ILocalDataSource
import com.example.githubsearchrepository.data.source.remote.api.RemoteDataSource

import com.example.githubsearchrepository.model.Repo
import com.example.githubsearchrepository.utils.Constants.NETWORK_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository  @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: ILocalDataSource

    ):IRepository {

    override fun getSearchResultStream(query: String): Flow<PagingData<Repo>> {
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { localDataSource.databaseObject.reposDao().reposByName(dbQuery) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = GithubRemoteMediator(
                query,
                remoteDataSource.githubServiceObject,
                localDataSource.databaseObject
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
    // before using database
    fun getSearchResultStream1(query: String): Flow<PagingData<Repo>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),

            pagingSourceFactory = { GithubPagingSource(remoteDataSource.githubServiceObject, query) }
        ).flow
    }

}

