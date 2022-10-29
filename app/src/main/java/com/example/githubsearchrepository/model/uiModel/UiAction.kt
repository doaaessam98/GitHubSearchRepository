package com.example.githubsearchrepository.model.uiModel

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()


}