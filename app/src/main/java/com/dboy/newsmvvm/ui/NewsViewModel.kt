package com.dboy.newsmvvm.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dboy.newsmvvm.api.response.Article
import com.dboy.newsmvvm.repositories.NewsRepository
import com.dboy.newsmvvm.util.COUNTRY_KEY
import com.dboy.newsmvvm.util.CountryCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _countryCode = MutableLiveData<CountryCode>()
    val countryCode: LiveData<CountryCode> = _countryCode
    val breakingNewsWithPagination = _countryCode.switchMap {
        newsRepository.getBreakingNewsWithPagination(it).cachedIn(viewModelScope)
    }

    // ^ this lambda will be executed whenever countryCode value changes
    //it's necessary to cacheIn the data, otherwise it will crash when rotating the device
    private val searchQuery = MutableLiveData<String>()
    val searchedNewsWithPagination: LiveData<PagingData<Article>> = searchQuery.switchMap {
        if (it.isEmpty()) {
            val emptyLiveData = MutableLiveData<PagingData<Article>>()
            emptyLiveData.value = PagingData.empty()
            emptyLiveData
        } else {
            newsRepository.searchNewsFromApiWithPagination(it, _countryCode.value ?: DEFAULT_COUNTRY_CODE).cachedIn(viewModelScope)
        }
    }

    init {
        viewModelScope.launch {
            val currentCountry = getCurrentCountryOnPreferences(COUNTRY_KEY)
            _countryCode.value = if (currentCountry != null) getCountry(currentCountry) else DEFAULT_COUNTRY_CODE
        }
    }

    companion object {
        private val DEFAULT_COUNTRY_CODE = CountryCode.us
    }

    fun searchNews(searchQuery: String) {
        this.searchQuery.value = searchQuery
    }

    private fun getCountry(code: String): CountryCode {
        return when (code) {
            "ar" -> CountryCode.ar
            "br" -> CountryCode.br
            "fr" -> CountryCode.fr
            "mx" -> CountryCode.mx
            "us" -> CountryCode.us
            else -> CountryCode.us
        }
    }

    fun changeCountry(countryCode: CountryCode) {
        this._countryCode.value = countryCode
        viewModelScope.launch {
            saveCountryOnPreferences(COUNTRY_KEY, countryCode.toString())
        }
    }


    private suspend fun saveCountryOnPreferences(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    private suspend fun getCurrentCountryOnPreferences(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    fun saveNews(article: Article) = viewModelScope.launch {
        newsRepository.upsertArticle(article)
    }

    fun deleteNews(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()
}