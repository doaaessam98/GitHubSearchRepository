package com.example.githubsearchrepository.data.paging


import android.content.ContentValues.TAG
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.githubsearchrepository.data.source.remote.api.GithubService
import com.example.githubsearchrepository.data.source.remote.api.IN_QUALIFIER
import com.example.githubsearchrepository.model.Repo
import com.example.githubsearchrepository.utils.Constants.GITHUB_STARTING_PAGE_INDEX
import com.example.githubsearchrepository.utils.Constants.NETWORK_PAGE_SIZE
import retrofit2.HttpException
import java.io.IOException




class GithubPagingSource(
    private val service: GithubService,
    private val query: String
): PagingSource<Int, Repo>() {
    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }



    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        Log.e(TAG, "loadmethod:${params.key}...........${params.loadSize} ", )
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
        val apiQuery = query + IN_QUALIFIER
        return  try {
            val response = service.searchRepos(apiQuery, position, params.loadSize)
            val repos = response.items
            Log.e(TAG, "load: ${repos}", )
        val nextKey =if (repos.isEmpty()) {
            null
        } else {
            position + (params.loadSize / NETWORK_PAGE_SIZE)
        }
        LoadResult.Page(
            data = repos,
            prevKey = if (position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
            nextKey = nextKey
        )
    } catch (exception: IOException){
          return LoadResult.Error(exception)

    } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }

    }
    }
