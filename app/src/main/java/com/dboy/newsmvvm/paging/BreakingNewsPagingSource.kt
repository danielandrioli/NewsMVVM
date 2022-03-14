package com.dboy.newsmvvm.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dboy.newsmvvm.api.NewsApi
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.util.NEWS_STARTING_PAGE_INDEX
import okio.IOException
import retrofit2.HttpException
import java.util.concurrent.CancellationException


class BreakingNewsPagingSource(
    private val newsApi: NewsApi,
    private val countryCode: CountryCode
): PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX

        return try {
            val response = newsApi.getBreakingNews(countryCode = countryCode.toString(), pageNumber = position,
                pageSize = params.loadSize)  //o tamanho loadSize é definido através do PagingConfig
            val newsResponse = response.body()
            if (response.isSuccessful && newsResponse != null){
                Log.i("BreakingNewsPaging", "successfull response: $response")
                LoadResult.Page(
                    data = newsResponse.articles,
                    prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (newsResponse.articles.isEmpty()) null else position + 1
                )
            } else{
                Log.i("BreakingNewsPaging", "Erro: $response")
                throw Exception(response.code().toString())
            }
        } catch (exception: IOException){ //thrown when there's no internet connection
            LoadResult.Error(exception)
        } catch (exception: HttpException){ //thrown when there's something wrong with the request.
            LoadResult.Error(exception)
        } catch (exception: Exception){ //for general errors not included in the two above
            if (exception is CancellationException) throw exception
            LoadResult.Error(exception)
        }
    }
/* About the CancellationException thrown above:
* if the coroutine gets cancelled in the middle of the try block, throwing this exception in the catch block will make the scope handle it.
* This is necessary otherwise the catch that catches the general Exception would handle the CancellationException.
* */
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
//        return state.anchorPosition
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
        }
    }
}