package com.dboy.newsmvvm.repositories

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dboy.newsmvvm.api.NewsApi
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.database.ArticleDao
import com.dboy.newsmvvm.paging.BreakingNewsPagingSource
import com.dboy.newsmvvm.paging.SearchNewsPagingSource
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.util.Language

class DefaultNewsRepository(
    private val articleDao: ArticleDao,
    private val newsApi: NewsApi
) : NewsRepository {
    override fun getBreakingNewsWithPagination(
        countryCode: CountryCode
    ): LiveData<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                BreakingNewsPagingSource(newsApi, countryCode)
            }
        ).liveData
    }

    override fun searchNewsFromApiWithPagination(
        searchQuery: String,
        country: CountryCode
    ): LiveData<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchNewsPagingSource(newsApi, country.language, searchQuery)
            }
        ).liveData
    }

    override suspend fun upsertArticle(article: Article): Long = articleDao.upsertArticle(article)

    override suspend fun deleteArticle(article: Article) {
        articleDao.deleteArticle(article)
    }

    override fun getSavedNews(): LiveData<List<Article>> = articleDao.getAllArticles()

}