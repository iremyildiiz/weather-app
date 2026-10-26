package com.example.havadurumu.data

import com.example.havadurumu.model.CitySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {

    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "tr",
        @Query("format") format: String = "json"
    ): CitySearchResponse
}