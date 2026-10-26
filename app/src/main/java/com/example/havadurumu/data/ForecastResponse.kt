package com.example.havadurumu.data

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val current: CurrentDto,
    val hourly: HourlyDto,
    val daily: DailyDto
)

data class CurrentDto(
    val time: String,

    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("weather_code")
    val weatherCode: Int,

    @SerializedName("apparent_temperature")
    val apparentTemperature: Double?,

    @SerializedName("relative_humidity_2m")
    val humidity: Int?,

    @SerializedName("wind_speed_10m")
    val windSpeed: Double?
)

data class HourlyDto(
    val time: List<String>,

    @SerializedName("temperature_2m")
    val temperature: List<Double>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>,

    @SerializedName("precipitation_probability")
    val precipitationProbability: List<Int?>
)

data class DailyDto(
    val time: List<String>,

    @SerializedName("temperature_2m_min")
    val minTemperature: List<Double>,

    @SerializedName("temperature_2m_max")
    val maxTemperature: List<Double>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>,

    @SerializedName("precipitation_probability_max")
    val precipitationProbability: List<Int?>
)