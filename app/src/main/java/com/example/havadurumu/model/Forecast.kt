package com.example.havadurumu.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Bir şehrin anlık durumu + önümüzdeki saatler ve günler.
 *
 * Tahmin ekranının veri modeli: bir şehre dokunulduğunda gösterilen bilgiler.
 * Zamanlar `timezone=auto` sayesinde şehrin yerel saatindedir.
 */
data class Forecast(
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)

/**
 * Şu anki hava.
 */
data class CurrentWeather(
    val temperatureC: Double,
    val weatherCode: Int,
    val observedAt: LocalDateTime,
    val apparentTemperatureC: Double? = null,
    val humidityPercent: Int? = null,
    val windSpeedKmh: Double? = null
)

/** Tek bir saatlik tahmin satırı. */
data class HourlyForecast(
    val time: LocalDateTime,
    val temperatureC: Double,
    val weatherCode: Int,
    val precipitationProbability: Int? = null
)

/** Tek bir günlük tahmin satırı. */
data class DailyForecast(
    val date: LocalDate,
    val minTemperatureC: Double,
    val maxTemperatureC: Double,
    val weatherCode: Int,
    val precipitationProbability: Int? = null
)