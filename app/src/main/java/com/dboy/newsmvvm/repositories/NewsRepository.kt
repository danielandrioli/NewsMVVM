package com.dboy.newsmvvm.repositories

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.util.Language

interface NewsRepository {
    fun getBreakingNewsWithPagination(countryCode: CountryCode): LiveData<PagingData<Article>> //no need for suspend fun here

    fun searchNewsFromApiWithPagination(
        searchQuery: String,
        country: CountryCode
    ): LiveData<PagingData<Article>>

    suspend fun upsertArticle(article: Article): Long

    suspend fun deleteArticle(article: Article)

    fun getSavedNews(): LiveData<List<Article>>
}

//O motivo para ter uma interface de repositório é poder testá-lo com um repositório fake.