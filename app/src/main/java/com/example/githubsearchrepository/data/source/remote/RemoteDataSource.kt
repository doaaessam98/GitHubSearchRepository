package com.example.githubsearchrepository.data.source.remote.api


import com.example.githubsearchrepository.data.source.remote.IRemoteDataSource
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val githubService: GithubService):
    IRemoteDataSource {


    override val githubServiceObject: GithubService
        get() = githubService
}