package com.example.githubsearchrepository.data.source.remote.api

import retrofit2.http.GET
import retrofit2.http.Query
const val IN_QUALIFIER = "in:name,description"
interface GithubService {

    @GET("search/repositories?sort=stars")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RepoSearchResponse
}
//https://api.github.com/search/repositories?q={query}{&page,per_page,sort,order}