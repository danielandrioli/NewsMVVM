package com.dboy.newsmvvm.repositories

import android.util.Log
import com.dboy.newsmvvm.api.CountryCode
import com.dboy.newsmvvm.api.NewsApi
import com.dboy.newsmvvm.api.response.Language
import com.dboy.newsmvvm.api.response.NewsResponse
import com.dboy.newsmvvm.database.ArticleDao
import com.dboy.newsmvvm.util.Resource

class DefaultNewsRepository(
    val articleDao: ArticleDao,
    val newsApi: NewsApi
) : NewsRepository {

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

    override suspend fun searchNewsFromApi(
        searchQuery: String,
        pageNumber: Int,
        language: Language
    ): Resource<NewsResponse> {
        val response = newsApi.searchForNews(keyWords = searchQuery, pageNumber = pageNumber, language = language.toString())

        return try {
            val result = response.body()
            if (result != null && response.isSuccessful) {
                Log.i("Search", "tudo ok - ${result.status}. Response: ${response.message()}")
                Resource.Success(result)
            } else {
                Log.i("Search", "Não deu: ${result?.status}. Response: ${response}")

                Resource.Error(response.message(), result)
            }
        } catch (e: Exception) {
            Log.i("Search", "Não deu, caiu no catch. Response: ${response.message()}")

            Resource.Error(e.message.toString())
        }
    }

}