package com.example.githubsearchrepository.ui

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearchrepository.databinding.ReposLoadStateFooterBinding

class ReposLoadStateAdapter(private val retry: () -> Unit):LoadStateAdapter<ReposLoadStateAdapter.ReposLoadStateViewHolder>() {

     override fun onCreateViewHolder(
         parent: ViewGroup,
         loadState: LoadState
     ): ReposLoadStateViewHolder {
          val view = ReposLoadStateFooterBinding.inflate(
              LayoutInflater.from(parent.context),parent,false)
         return ReposLoadStateViewHolder(view)
     }


     override fun onBindViewHolder(holder: ReposLoadStateViewHolder, loadState: LoadState) {

         if (loadState is LoadState.Error) {
            holder. binding.errorMsg.text =" something error happen"
             Log.e(TAG, "onBindViewHolder: ${loadState.error.localizedMessage}", )
         }
         holder.binding.progressBar.isVisible = loadState is LoadState.Loading
         holder. binding.retryButton.isVisible = loadState is LoadState.Error
         holder. binding.errorMsg.isVisible = loadState is LoadState.Error

        holder. binding.retryButton.setOnClickListener { retry.invoke() }
     }


     class ReposLoadStateViewHolder(var binding: ReposLoadStateFooterBinding)
        :RecyclerView.ViewHolder(binding.root)
}