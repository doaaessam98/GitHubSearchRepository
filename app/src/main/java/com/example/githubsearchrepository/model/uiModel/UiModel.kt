package com.example.githubsearchrepository.model.uiModel

import com.example.githubsearchrepository.model.Repo

sealed class UiModel {
    data class RepoItem(val repo: Repo) : UiModel()
    data class SeparatorItem(val description: String) : UiModel()

}