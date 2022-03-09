package com.dboy.newsmvvm.repositories

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.Language
import com.dboy.newsmvvm.api.response.NewsResponse
import com.dboy.newsmvvm.util.Resource

interface NewsRepository {
//    suspend fun getBreakingNewsFromApi(countryCode: CountryCode, pageNumber: Int): Resource<NewsResponse>
    fun getBreakingNewsWithPagination(countryCode: CountryCode): LiveData<PagingData<Article>> //no need for suspend fun here

    fun searchNewsFromApiWithPagination(searchQuery: String, language: Language): LiveData<PagingData<Article>>

    suspend fun searchNewsFromApi(searchQuery: String, pageNumber: Int, language: Language): Resource<NewsResponse>

    suspend fun upsertArticle(article: Article): Long

    suspend fun deleteArticle(article: Article)

    fun getSavedNews(): LiveData<List<Article>>
}

//O motivo para ter uma interface de repositório é poder testá-lo com um repositório fake.