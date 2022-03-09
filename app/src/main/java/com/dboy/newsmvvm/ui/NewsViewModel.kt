package com.dboy.newsmvvm.ui

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.Language
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
//    var breakingNewsPage = 1
    private var language = DEFAULT_LANGUAGE
    private val countryCode = MutableLiveData(DEFAULT_COUNTRY_CODE)
    val breakingNewsWithPagination = countryCode.switchMap {
        //this lambda will be executed whenever countryCode value changes
        newsRepository.getBreakingNewsWithPagination(it).cachedIn(viewModelScope)
    } //it's necessary to chacheIn the data, otherwise it will crash when rotating the device
    private val _searchedNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchedNews: LiveData<Resource<NewsResponse>> = _searchedNews
    private val searchQuery = MutableLiveData<String>()
    val searchedNewsWithPagination = searchQuery.switchMap {
        newsRepository.searchNewsFromApiWithPagination(it, language)
    }
//    var searchNewsPage = 1

    init {
        getBreakingNews(DEFAULT_COUNTRY_CODE)
    }

    companion object {
        private val DEFAULT_COUNTRY_CODE = CountryCode.us //pegar do shared preferences!
        private val DEFAULT_LANGUAGE = when(DEFAULT_COUNTRY_CODE) {
            CountryCode.br -> Language.pt
            CountryCode.ar -> Language.es
            CountryCode.fr -> Language.fr
            CountryCode.mx -> Language.es
            CountryCode.us -> Language.en
        }

    }

    fun changeCountry(countryCode: CountryCode){
        this.countryCode.value = countryCode
    }


    fun getBreakingNews(countryCode: CountryCode) {

//        viewModelScope.launch(Dispatchers.IO) {
//            _breakingNews.postValue(Resource.Loading())
//            val result = newsRepository.getBreakingNewsFromApi(countryCode, breakingNewsPage)
//            _breakingNews.postValue(result)  //o Resource será Success ou Error. A UI será notificada pelo LiveData quando houver mudança.
//        }
    }

    fun searchNews(searchQuery: String, language: Language) {
        this.language = language
        this.searchQuery.value = searchQuery
        /*
        viewModelScope.launch(Dispatchers.IO) {
            _searchedNews.postValue(Resource.Loading())
            val result = newsRepository.searchNewsFromApi(searchQuery = searchQuery, language = language, pageNumber = searchNewsPage)
            _searchedNews.postValue(result)
        }
         */
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    fun deleteNews(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()
}