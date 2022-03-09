package com.dboy.newsmvvm.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.api.NewsApi
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.Language
import com.dboy.newsmvvm.api.response.NewsResponse
import com.dboy.newsmvvm.database.ArticleDao
import com.dboy.newsmvvm.paging.BreakingNewsPagingSource
import com.dboy.newsmvvm.paging.SearchNewsPagingSource
import com.dboy.newsmvvm.util.Resource

class DefaultNewsRepository(
    private val articleDao: ArticleDao,
    private val newsApi: NewsApi
) : NewsRepository {
    /*  //Old function used before pagination implementation.
        override suspend fun getBreakingNewsFromApi(
            countryCode: CountryCode,
            pageNumber: Int
        ): Resource<NewsResponse> {
            val response = newsApi.getBreakingNews(
                countryCode = countryCode.toString(),
                pageNumber = pageNumber
            ) //response contém o Response<NewsResponse>

            return try {
                val result = response.body() // body() devolve o NewsResponse
                if (result != null && response.isSuccessful) {
                    Resource.Success(result)
                } else {
                    Resource.Error(response.message(), result)
                }
            } catch (e: Exception) {
                Resource.Error(e.message.toString())
            }
        }
     */
    override fun getBreakingNewsWithPagination(
        countryCode: CountryCode
    ): LiveData<PagingData<Article>>{
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
        language: Language
    ): LiveData<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchNewsPagingSource(newsApi, language, searchQuery)
            }
        ).liveData
    }

    override suspend fun searchNewsFromApi(
        searchQuery: String,
        pageNumber: Int,
        language: Language
    ): Resource<NewsResponse> {
        val response = newsApi.searchForNews(
            keyWords = searchQuery,
            pageNumber = pageNumber,
            language = language.toString()
        )

        return try {
            val result = response.body()
            if (result != null && response.isSuccessful) {
                Log.i("Search", "tudo ok - ${result.status}. Response: ${response.message()}")
                Resource.Success(result)
            } else {
                Log.i("Search", "Não deu: ${result?.status}. Response: $response")
                Resource.Error(response.message(), result)
            }
        } catch (e: Exception) {
            Log.i("Search", "Não deu, caiu no catch. Response: ${response.message()}")
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun upsertArticle(article: Article): Long = articleDao.upsertArticle(article)

    override suspend fun deleteArticle(article: Article) {
        articleDao.deleteArticle(article)
    }

    override fun getSavedNews(): LiveData<List<Article>> = articleDao.getAllArticles()

}