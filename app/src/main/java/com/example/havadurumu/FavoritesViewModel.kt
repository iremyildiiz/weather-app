package com.example.havadurumu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.havadurumu.data.FavoritesRepository
import com.example.havadurumu.data.WeatherRepository
import com.example.havadurumu.model.City
import com.example.havadurumu.model.Forecast
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoriteWeather(
    val city: City,
    val forecast: Forecast? = null,
    val isLoading: Boolean = true,
    val error: Boolean = false
)

class FavoritesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        FavoritesRepository(application)

    private val weatherRepository =
        WeatherRepository()

    val favorites: StateFlow<List<City>> =
        repository.favorites.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _favoriteWeather =
        MutableStateFlow<List<FavoriteWeather>>(emptyList())

    val favoriteWeather: StateFlow<List<FavoriteWeather>> =
        _favoriteWeather.asStateFlow()

    fun loadFavoriteWeather() {

        viewModelScope.launch {

            val cities = favorites.value

            if (cities.isEmpty()) {
                _favoriteWeather.value = emptyList()
                return@launch
            }

            _favoriteWeather.value =
                cities.map { city ->
                    FavoriteWeather(
                        city = city,
                        isLoading = true
                    )
                }

            val results = cities.map { city ->

                async {

                    try {

                        val forecast =
                            weatherRepository.getForecast(
                                latitude = city.latitude,
                                longitude = city.longitude
                            )

                        FavoriteWeather(
                            city = city,
                            forecast = forecast,
                            isLoading = false,
                            error = false
                        )

                    } catch (e: Exception) {

                        FavoriteWeather(
                            city = city,
                            forecast = null,
                            isLoading = false,
                            error = true
                        )
                    }
                }
            }.awaitAll()

            _favoriteWeather.value = results
        }
    }

    fun addFavorite(city: City) {

        if (favorites.value.none {
                it.id == city.id
            }
        ) {

            val updatedFavorites =
                favorites.value + city

            saveFavorites(updatedFavorites)
        }
    }

    fun removeFavorite(city: City) {

        val updatedFavorites =
            favorites.value.filterNot {
                it.id == city.id
            }

        saveFavorites(updatedFavorites)

        _favoriteWeather.value =
            _favoriteWeather.value.filterNot {
                it.city.id == city.id
            }
    }

    fun toggleFavorite(city: City) {

        if (isFavorite(city)) {
            removeFavorite(city)
        } else {
            addFavorite(city)
        }
    }

    fun isFavorite(city: City): Boolean {

        return favorites.value.any {
            it.id == city.id
        }
    }

    private fun saveFavorites(
        cities: List<City>
    ) {

        viewModelScope.launch {
            repository.saveFavorites(cities)
        }
    }
}