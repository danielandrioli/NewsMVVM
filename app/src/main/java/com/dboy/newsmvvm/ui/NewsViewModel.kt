package com.dboy.newsmvvm.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dboy.newsmvvm.api.CountryCode
import com.dboy.newsmvvm.api.response.Language
import com.dboy.newsmvvm.api.response.NewsResponse
import com.dboy.newsmvvm.repositories.NewsRepository
import com.dboy.newsmvvm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val breakingNews: LiveData<Resource<NewsResponse>> = _breakingNews
    var breakingNewsPage = 1

    private val _searchedNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchedNews: LiveData<Resource<NewsResponse>> = _searchedNews
    var searchNewsPage = 1

    init {
        getBreakingNews(CountryCode.us)
    }
    //talvez mudar o tipo do countryCode para um ENUM que contenha os códigos
    fun getBreakingNews(countryCode: CountryCode) {
        viewModelScope.launch(Dispatchers.IO) {
            _breakingNews.postValue(Resource.Loading())
            val result = newsRepository.getBreakingNewsFromApi(countryCode, breakingNewsPage)
            _breakingNews.postValue(result)  //o Resource será Success ou Error. A UI será notificada pelo LiveData quando houver mudança.
        }
    }

    fun searchNews(searchQuery: String, language: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchedNews.postValue(Resource.Loading())
            val result = newsRepository.searchNewsFromApi(searchQuery = searchQuery, language = language, pageNumber = searchNewsPage)
            _searchedNews.postValue(result)
        }
    }
}