package com.example.healthyme.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthyme.Data.Model.NewsResponse
import com.example.healthyme.Data.Repository.NewsRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _newsLiveData = MutableLiveData<Response<NewsResponse>>()
    val newsLiveData: LiveData<Response<NewsResponse>> = _newsLiveData

    fun fetchNews(query: String, country: String, apiKey: String) {
        viewModelScope.launch {
            val response = repository.getNews(query, country, apiKey)
            _newsLiveData.postValue(response)
        }
    }
}
