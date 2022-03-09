package com.dboy.newsmvvm.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dboy.newsmvvm.api.NewsApi
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.Language
import com.dboy.newsmvvm.util.NEWS_STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.CancellationException

class SearchNewsPagingSource(
    private val newsApi: NewsApi,
    private val language: Language,
    private val query: String
) : PagingSource<Int, Article>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX

        return try {
            val response = newsApi.searchForNews(
                keyWords = query, language = language.toString(), pageNumber = position,
                pageSize = params.loadSize
            )
            val searchNewsResponse = response.body()
            if (response.isSuccessful && searchNewsResponse != null) {
                LoadResult.Page(
                    data = searchNewsResponse.articles,
                    prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (searchNewsResponse.articles.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Invalid()
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            if (exception is CancellationException) {
                throw exception
            }
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        TODO("Not yet implemented")
    }
}