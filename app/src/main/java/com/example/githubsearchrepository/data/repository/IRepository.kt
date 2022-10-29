package com.example.githubsearchrepository.data.repository

import androidx.paging.PagingData
import com.example.githubsearchrepository.model.Repo
import kotlinx.coroutines.flow.Flow

interface IRepository {


     fun getSearchResultStream(query: String): Flow<PagingData<Repo>>

}