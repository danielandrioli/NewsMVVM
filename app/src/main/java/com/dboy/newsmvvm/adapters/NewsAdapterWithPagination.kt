package com.dboy.newsmvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.databinding.NewsItemBinding

class NewsAdapterWithPagination :
    PagingDataAdapter<Article, NewsAdapterWithPagination.NewsViewHolder>(
        differCallBack
    ) {

    inner class NewsViewHolder(val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var onItemArticleClickListener: ((Article) -> Unit)? = null

    fun setOnItemArticleClickListener(listener: (Article) -> Unit) {
        onItemArticleClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)

        if (article != null) {
            holder.binding.apply {
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvSource.text = article.source?.name
                tvPublishedAt.text = article.publishedAt
                ivArtigo.load(article.urlToImage)
            }

            holder.itemView.setOnClickListener {
                onItemArticleClickListener?.let {
                    it(article)
                }
            }
        }
    }
}