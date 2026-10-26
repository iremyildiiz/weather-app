package com.example.havadurumu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.havadurumu.data.CityRepository
import com.example.havadurumu.model.City
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityViewModel(
    private val repository: CityRepository = CityRepository()
) : ViewModel() {

    private val _selectedCity = MutableStateFlow(
        City(
            id = -1L,
            name = "İstanbul",
            latitude = 41.0082,
            longitude = 28.9784,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "İstanbul"
        )
    )

    val selectedCity: StateFlow<City> =
        _selectedCity.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> =
        _query.asStateFlow()

    private val _cities =
        MutableStateFlow<List<City>>(emptyList())

    val cities: StateFlow<List<City>> =
        _cities.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    private var searchJob: Job? = null

    fun selectCity(city: City) {
        _selectedCity.value = city
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery

        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _cities.value = emptyList()
            _errorMessage.value = null
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(400)

            _isLoading.value = true
            _errorMessage.value = null

            try {
                _cities.value =
                    repository.searchCities(newQuery)
            } catch (e: Exception) {
                _cities.value = emptyList()
                _errorMessage.value =
                    "Şehirler yüklenirken bir hata oluştu."
            } finally {
                _isLoading.value = false
            }
        }
    }
}