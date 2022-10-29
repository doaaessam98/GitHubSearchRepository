package com.example.githubsearchrepository.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearchrepository.databinding.SeparatorViewItemBinding

class SeparatorViewHolder (var binding: SeparatorViewItemBinding): RecyclerView.ViewHolder(binding.root){
      fun bind(description: String) {
             binding.separatorDescription.text=description
      }


    companion object {
        fun create(parent: ViewGroup): SeparatorViewHolder {
            val separatorView =
                SeparatorViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SeparatorViewHolder(separatorView)
        }
    }

}
