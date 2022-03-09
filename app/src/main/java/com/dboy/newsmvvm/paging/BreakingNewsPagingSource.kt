package com.dboy.newsmvvm.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dboy.newsmvvm.api.NewsApi
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.util.NEWS_STARTING_PAGE_INDEX
import okio.IOException
import retrofit2.HttpException
import java.util.concurrent.CancellationException


class BreakingNewsPagingSource( //por enquanto, vou deixar sem injecao de dependencia. Mas posteriormente vou mexer nisso
    private val newsApi: NewsApi,
    private val countryCode: CountryCode  //se eu colocar um parâmetro padrão, talvez consiga fazer com que o Hilt injete aqui.
): PagingSource<Int, Article>() {           //ou posso fazer o countryCode vir da SharedPreferences, q pode ser mudado depois pelo usuário.

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val position = params.key ?: NEWS_STARTING_PAGE_INDEX

        return try {
            val response = newsApi.getBreakingNews(countryCode = countryCode.toString(), pageNumber = position,
                pageSize = params.loadSize)  //o tamanho loadSize é definino através do PagingConfig
            val newsResponse = response.body()
            if (response.isSuccessful && newsResponse != null){
                LoadResult.Page(
                    data = newsResponse.articles,
                    prevKey = if (position == NEWS_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (newsResponse.articles.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Invalid()
            }
        } catch (exception: IOException){ //thrown when there's no internet connection
            LoadResult.Error(exception)
        } catch (exception: HttpException){ //thrown when there's something wrong with the request.
            LoadResult.Error(exception)
        } catch (exception: Exception){ //for general errors not included in the two above
            if (exception is CancellationException){
                throw exception //if the coroutine gets cancelled in the middle of the try block, throwing this exception in the
            }                   //catch block will make the scope handle it. This is necessary otherwise the general catch with Exception
            LoadResult.Error(exception)  //would handle it.
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        TODO("Not yet implemented")
    }
}