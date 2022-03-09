package com.dboy.newsmvvm.api

import com.dboy.newsmvvm.api.response.NewsResponse
import com.dboy.newsmvvm.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("/v2/top-headlines") //esse é um headline
    suspend fun getBreakingNews(
        @Query("category") category: String = "general",
        @Query("country") countryCode: String = "br",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY,
    ): Response<NewsResponse>

    @GET("/v2/everything")
    suspend fun searchForNews(
        @Query("q") keyWords: String,
        @Query("searchIn") searchIn: String = "title,description,content",
        @Query("language") language: String = "pt",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY,
    ): Response<NewsResponse>
}

/*
Aqui é possível ver o "request parameters" que a documentação da API pede para as breaking news (top-headlines):
https://newsapi.org/docs/endpoints/top-headlines
*/
