package com.dboy.newsmvvm.repositories

import androidx.lifecycle.LiveData
import com.dboy.newsmvvm.api.CountryCode
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.api.response.Language
import com.dboy.newsmvvm.api.response.NewsResponse
import com.dboy.newsmvvm.util.Resource

interface NewsRepository {
    suspend fun getBreakingNewsFromApi(countryCode: CountryCode, pageNumber: Int): Resource<NewsResponse>

    suspend fun searchNewsFromApi(searchQuery: String, pageNumber: Int, language: Language): Resource<NewsResponse>

    suspend fun upsertArticle(article: Article): Long

    suspend fun deleteArticle(article: Article)

    fun getSavedNews(): LiveData<List<Article>>
}

//O motivo para ter uma interface de repositório é poder testá-lo com um repositório fake.