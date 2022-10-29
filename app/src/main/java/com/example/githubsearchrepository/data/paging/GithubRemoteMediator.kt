package com.example.githubsearchrepository.data.paging

import androidx.paging.*
import androidx.room.withTransaction
import com.example.githubsearchrepository.data.source.local.db.RepoDatabase
import com.example.githubsearchrepository.data.source.remote.api.GithubService
import com.example.githubsearchrepository.data.source.remote.api.IN_QUALIFIER

import com.example.githubsearchrepository.model.RemoteKeys
import com.example.githubsearchrepository.model.Repo
import com.example.githubsearchrepository.utils.Constants.GITHUB_STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val service: GithubService,
    private val repoDatabase: RepoDatabase

) : RemoteMediator<Int, Repo>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Repo>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: GITHUB_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)

                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        val apiQuery = query + IN_QUALIFIER
        try {
            val response = service.searchRepos(apiQuery, page, state.config.pageSize)
            val repos = response.items
            val endOfPaginationReached = repos.isEmpty()
             repoDatabase.withTransaction {
                 if (loadType == LoadType.REFRESH) {
                      repoDatabase.remoteKeysDao().clearRemoteKeys()
                       repoDatabase.reposDao().clearRepos()
                 }
                 val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
                 val nextKey = if (endOfPaginationReached) null else page + 1
                 val keys = repos.map {
                     RemoteKeys(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                 }
                 repoDatabase.remoteKeysDao().insertAll(keys)
                 repoDatabase.reposDao().insertAll(repos)
             }
            return MediatorResult.Success(endOfPaginationReached=endOfPaginationReached)
        } catch (exception: IOException) {
            return RemoteMediator.MediatorResult.Error(exception)

        } catch (exception: HttpException) {
            return RemoteMediator.MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repo.id)
            }

    }


    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repoId)
            }
        }
    }
}