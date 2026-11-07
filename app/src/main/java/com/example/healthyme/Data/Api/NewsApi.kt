package com.example.healthyme.Data.Api

import com.example.healthyme.Data.Model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/everything")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>
}