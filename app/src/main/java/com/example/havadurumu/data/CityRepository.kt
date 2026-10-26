package com.example.havadurumu.data

import com.example.havadurumu.model.City

class CityRepository(
    private val api: CityApi = ApiClient.cityApi
) {

    suspend fun searchCities(query: String): List<City> {

        if (query.isBlank()) {
            return emptyList()
        }

        val response = api.searchCities(
            name = query
        )

        return response.results.orEmpty()
    }
}