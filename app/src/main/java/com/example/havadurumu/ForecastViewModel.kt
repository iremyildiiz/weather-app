package com.example.havadurumu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havadurumu.data.WeatherRepository
import com.example.havadurumu.model.Forecast
import com.example.havadurumu.ui.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForecastViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _forecastState =
        MutableStateFlow<LoadState<Forecast>>(LoadState.Idle)

    val forecastState: StateFlow<LoadState<Forecast>> =
        _forecastState.asStateFlow()

    fun loadForecast(
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {

            _forecastState.value = LoadState.Loading

            try {
                val forecast = repository.getForecast(
                    latitude = latitude,
                    longitude = longitude
                )

                _forecastState.value =
                    LoadState.Success(forecast)

            } catch (e: Exception) {

                _forecastState.value =
                    LoadState.Error(
                        "Hava durumu yüklenirken bir hata oluştu."
                    )
            }
        }
    }
}