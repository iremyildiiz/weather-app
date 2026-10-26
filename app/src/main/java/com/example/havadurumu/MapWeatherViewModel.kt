package com.example.havadurumu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havadurumu.data.WeatherRepository
import com.example.havadurumu.model.City
import com.example.havadurumu.model.Forecast
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapCityWeather(
    val city: City,
    val forecast: Forecast? = null,
    val isLoading: Boolean = true,
    val error: Boolean = false
)

class MapWeatherViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val mapCities = listOf(
        City(
            id = 34L,
            name = "İstanbul",
            latitude = 41.0082,
            longitude = 28.9784,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "İstanbul"
        ),
        City(
            id = 6L,
            name = "Ankara",
            latitude = 39.9334,
            longitude = 32.8597,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Ankara"
        ),
        City(
            id = 35L,
            name = "İzmir",
            latitude = 38.4237,
            longitude = 27.1428,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "İzmir"
        ),
        City(
            id = 16L,
            name = "Bursa",
            latitude = 40.1950,
            longitude = 29.0600,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Bursa"
        ),
        City(
            id = 7L,
            name = "Antalya",
            latitude = 36.8969,
            longitude = 30.7133,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Antalya"
        ),
        City(
            id = 55L,
            name = "Samsun",
            latitude = 41.2867,
            longitude = 36.3300,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Samsun"
        ),
        City(
            id = 21L,
            name = "Diyarbakır",
            latitude = 37.9144,
            longitude = 40.2306,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Diyarbakır"
        )
    )

    private val _cityWeather =
        MutableStateFlow<List<MapCityWeather>>(emptyList())

    val cityWeather: StateFlow<List<MapCityWeather>> =
        _cityWeather.asStateFlow()

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {

            _cityWeather.value = mapCities.map { city ->
                MapCityWeather(
                    city = city,
                    isLoading = true
                )
            }

            val results = mapCities.map { city ->
                async {
                    try {
                        val forecast = weatherRepository.getForecast(
                            latitude = city.latitude,
                            longitude = city.longitude
                        )

                        MapCityWeather(
                            city = city,
                            forecast = forecast,
                            isLoading = false,
                            error = false
                        )

                    } catch (e: Exception) {

                        MapCityWeather(
                            city = city,
                            forecast = null,
                            isLoading = false,
                            error = true
                        )
                    }
                }
            }.awaitAll()

            _cityWeather.value = results
        }
    }
}