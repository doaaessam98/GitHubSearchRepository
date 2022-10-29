package com.example.githubsearchrepository.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.githubsearchrepository.R

import com.example.githubsearchrepository.model.Repo
import com.example.githubsearchrepository.model.uiModel.UiModel

class ReposAdapter: PagingDataAdapter<UiModel,ViewHolder>(UIMODEL_COMPARATOR) {
    private var repo: Repo? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  when (viewType) {
            R.layout.recycler_view_item -> RepoViewHolder.create(parent)
           else ->  SeparatorViewHolder.create(parent)
       }

    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.RepoItem -> R.layout.recycler_view_item
            is UiModel.SeparatorItem -> R.layout.separator_view_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel?.let {
            when (it) {
                is UiModel.RepoItem -> (holder as RepoViewHolder).bind(it.repo)
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(it.description)

            }
        }
    }





    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.RepoItem && newItem is UiModel.RepoItem &&
                        oldItem.repo.fullName == newItem.repo.fullName) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }


}