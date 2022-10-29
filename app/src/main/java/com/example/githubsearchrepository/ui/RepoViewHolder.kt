package com.example.githubsearchrepository.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearchrepository.R
import com.example.githubsearchrepository.databinding.RecyclerViewItemBinding
import com.example.githubsearchrepository.model.Repo

class RepoViewHolder (private val binding: RecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root){
    private var repo: Repo? = null
    init {
        itemView.setOnClickListener {
            repo?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(intent)
            }
        }
    }
    fun bind(repo: Repo?) {
        if (repo == null) {
            val resources = itemView.resources

            binding. repoName.text = resources.getString(R.string.loading)
            binding. repoDescription.visibility = View.GONE
            binding. repoLanguage.visibility = View.GONE
            binding.repoStars.text = resources.getString(R.string.unknown)
            binding.repoForks.text = resources.getString(R.string.unknown)
        } else {
            showRepoData(repo)
        }

    }
    private fun showRepoData( repo: Repo) {
        this.repo = repo
        binding.repoName.text = repo.fullName
        var descriptionVisibility = View.GONE
        if (repo.description != null) {
            binding.repoDescription.text = repo.description
            descriptionVisibility = View.VISIBLE
        }
        binding.repoDescription.visibility = descriptionVisibility

        binding.repoStars .text = repo.stars.toString()
        binding.repoForks .text = repo.forks.toString()

        var languageVisibility = View.GONE
        if (!repo.language.isNullOrEmpty()) {
            val resources = itemView .context.resources
            binding.repoLanguage.text = resources.getString(R.string.language, repo.language)
            languageVisibility = View.VISIBLE
        }
        binding.repoLanguage.visibility = languageVisibility
    }
    companion object {
        fun create(parent: ViewGroup): RepoViewHolder {
            val repoView = RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RepoViewHolder(repoView)
        }
    }
}
