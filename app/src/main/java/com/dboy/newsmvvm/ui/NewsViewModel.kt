package com.dboy.newsmvvm.ui

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dboy.newsmvvm.util.CountryCode
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.util.Language
import com.dboy.newsmvvm.repositories.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    private val countryCode = MutableLiveData<CountryCode>()
    private var language = DEFAULT_LANGUAGE
    val breakingNewsWithPagination = countryCode.switchMap {
        changeLanguage(it)
        newsRepository.getBreakingNewsWithPagination(it).cachedIn(viewModelScope)
    }
    // ^ this lambda will be executed whenever countryCode value changes
    //it's necessary to cacheIn the data, otherwise it will crash when rotating the device
    private val searchQuery = MutableLiveData<String>()
    val searchedNewsWithPagination: LiveData<PagingData<Article>> = searchQuery.switchMap {
        if (it.isEmpty()){
            val emptyLiveData = MutableLiveData<PagingData<Article>>()
            emptyLiveData.value = PagingData.empty()
            emptyLiveData
        } else {
            newsRepository.searchNewsFromApiWithPagination(it, language).cachedIn(viewModelScope)
        }
    }

    init {
        countryCode.value = DEFAULT_COUNTRY_CODE //PEGAR DO SHARED PREFERENCES
    }

    companion object {
        private val DEFAULT_COUNTRY_CODE = CountryCode.us //pegar do shared preferences!
        private val DEFAULT_LANGUAGE = Language.en
    }

    private fun changeLanguage(countryCode: CountryCode) {
        when (countryCode) {
            CountryCode.br -> Language.pt
            CountryCode.ar -> Language.es
            CountryCode.fr -> Language.fr
            CountryCode.mx -> Language.es
            CountryCode.us -> Language.en
        }
    }

    fun changeCountry(countryCode: CountryCode) {
        this.countryCode.value = countryCode
    }

    fun searchNews(searchQuery: String, language: Language) {
        this.language = language
        this.searchQuery.value = searchQuery
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    fun deleteNews(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()
}