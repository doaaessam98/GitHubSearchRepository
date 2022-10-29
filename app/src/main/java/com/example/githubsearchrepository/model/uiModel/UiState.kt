package com.example.githubsearchrepository.model.uiModel

import com.example.githubsearchrepository.utils.Constants.DEFAULT_QUERY


data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)
