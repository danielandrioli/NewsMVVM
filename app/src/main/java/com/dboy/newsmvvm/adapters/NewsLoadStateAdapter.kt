package com.dboy.newsmvvm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dboy.newsmvvm.databinding.NewsLoadStateFooterBinding

class NewsLoadStateAdapter(private val retry: () -> Unit): LoadStateAdapter<NewsLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(val binding: NewsLoadStateFooterBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnRetry.setOnClickListener {
                retry()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = NewsLoadStateFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.binding.apply {
            progressBarFooter.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            btnRetry.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            tvError.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
        }
    }
}