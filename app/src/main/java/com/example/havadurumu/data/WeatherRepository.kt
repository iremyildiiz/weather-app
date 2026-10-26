package com.example.havadurumu.data

import com.example.havadurumu.model.CurrentWeather
import com.example.havadurumu.model.DailyForecast
import com.example.havadurumu.model.Forecast
import com.example.havadurumu.model.HourlyForecast
import java.time.LocalDate
import java.time.LocalDateTime

class WeatherRepository(
    private val api: ForecastApi = ApiClient.forecastApi
) {

    suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ): Forecast {

        val response = api.getForecast(
            latitude = latitude,
            longitude = longitude
        )

        val current = CurrentWeather(
            temperatureC = response.current.temperature,
            weatherCode = response.current.weatherCode,
            observedAt = LocalDateTime.parse(response.current.time),
            apparentTemperatureC = response.current.apparentTemperature,
            humidityPercent = response.current.humidity,
            windSpeedKmh = response.current.windSpeed
        )

        val hourly = response.hourly.time.indices.map { index ->
            HourlyForecast(
                time = LocalDateTime.parse(
                    response.hourly.time[index]
                ),
                temperatureC = response.hourly.temperature[index],
                weatherCode = response.hourly.weatherCode[index],
                precipitationProbability =
                    response.hourly.precipitationProbability
                        .getOrNull(index)
            )
        }

        val daily = response.daily.time.indices.map { index ->
            DailyForecast(
                date = LocalDate.parse(
                    response.daily.time[index]
                ),
                minTemperatureC =
                    response.daily.minTemperature[index],
                maxTemperatureC =
                    response.daily.maxTemperature[index],
                weatherCode =
                    response.daily.weatherCode[index],
                precipitationProbability =
                    response.daily.precipitationProbability
                        .getOrNull(index)
            )
        }

        return Forecast(
            current = current,
            hourly = hourly,
            daily = daily
        )
    }
}