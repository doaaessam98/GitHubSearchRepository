package com.example.githubsearchrepository.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.githubsearchrepository.data.repository.IRepository
import com.example.githubsearchrepository.model.uiModel.UiAction
import com.example.githubsearchrepository.model.uiModel.UiModel
import com.example.githubsearchrepository.model.uiModel.UiState

import com.example.githubsearchrepository.utils.Constants.DEFAULT_QUERY
import com.example.githubsearchrepository.utils.Constants.LAST_QUERY_SCROLLED
import com.example.githubsearchrepository.utils.Constants.LAST_SEARCH_QUERY


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
 class SearchViewModel @Inject constructor(private val repository: IRepository,
                                           private val savedStateHandle: SavedStateHandle
            ):ViewModel() {

    val state :StateFlow<UiState>
    val pagingDataFlow: Flow<PagingData<UiModel>>
    val accept: (UiAction) -> Unit
    private val UiModel.RepoItem.roundedStarCount: Int
        get() = this.repo.stars / 10_000

    init {
        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }
        pagingDataFlow = searches
            .flatMapLatest {
                searchRepo(queryString = it.query) }
            .cachedIn(viewModelScope)

            state = combine(
                searches,
                queriesScrolled,
                ::Pair
            ).map { (search, scroll) ->
                UiState(
                    query = search.query,
                    lastQueryScrolled = scroll.currentQuery,
                    hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery)
            }

         .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }
    private  fun searchRepo(queryString: String): Flow<PagingData<UiModel>> =
        repository.getSearchResultStream(queryString).map { pagingData->
             pagingData.map { UiModel.RepoItem(it)} }.map {

               it.insertSeparators { before,after->

                   
                   if (after == null) {
                       return@insertSeparators null
                   }

                   if (before == null) {
                       return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                   }
                   // check between 2 items
                   if (before.roundedStarCount > after.roundedStarCount) {
                       if (after.roundedStarCount >= 1) {
                           UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                       } else {
                           UiModel.SeparatorItem("< 10.000+ stars")
                       }
                   } else {

                      null
                   }


               }
        }











    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }
}

