package com.example.havadurumu.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havadurumu.FavoritesViewModel
import com.example.havadurumu.ForecastViewModel
import com.example.havadurumu.R
import com.example.havadurumu.model.City
import com.example.havadurumu.model.DailyForecast
import com.example.havadurumu.model.HourlyForecast
import com.example.havadurumu.model.WeatherCondition
import com.example.havadurumu.ui.component.WeatherBackground
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ForecastScreen(
    latitude: Double,
    longitude: Double,
    cityName: String,
    viewModel: ForecastViewModel,
    favoritesViewModel: FavoritesViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val state by viewModel.forecastState.collectAsState()
    val favorites by favoritesViewModel.favorites.collectAsState()

    val city = City(
        id = cityName.hashCode().toLong(),
        name = cityName,
        latitude = latitude,
        longitude = longitude,
        countryCode = null,
        country = "Türkiye",
        admin1 = null
    )

    val isFavorite = favorites.any {
        it.name.equals(
            cityName,
            ignoreCase = true
        )
    }

    LaunchedEffect(
        latitude,
        longitude
    ) {

        viewModel.loadForecast(
            latitude = latitude,
            longitude = longitude
        )
    }

    when (
        val currentState = state
    ) {

        LoadState.Idle,
        LoadState.Loading -> {

            ForecastLoadingScreen(
                modifier = modifier
            )
        }

        is LoadState.Error -> {

            ForecastErrorScreen(
                message = currentState.message,
                onBackClick = onBackClick,
                modifier = modifier
            )
        }

        is LoadState.Success -> {

            val forecast =
                currentState.data

            val condition =
                WeatherCondition
                    .fromWeatherCode(
                        forecast.current.weatherCode
                    )

            val today =
                forecast.daily
                    .firstOrNull()

            val upcomingHours =
                forecast.hourly
                    .filter {

                        !it.time.isBefore(
                            forecast.current.observedAt
                        )
                    }
                    .take(12)

            val cityImage =
                forecastCityImageResource(
                    cityName
                )

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFFE7F2FC)
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {

                ForecastHero(
                    cityName = cityName,
                    imageResource = cityImage,
                    condition = condition,
                    temperature =
                        forecast.current.temperatureC,
                    apparentTemperature =
                        forecast.current.apparentTemperatureC,
                    humidity =
                        forecast.current.humidityPercent,
                    windSpeed =
                        forecast.current.windSpeedKmh,
                    maxTemperature =
                        today?.maxTemperatureC,
                    minTemperature =
                        today?.minTemperatureC,
                    isFavorite = isFavorite,
                    onBackClick = onBackClick,
                    onFavoriteClick = {

                        favoritesViewModel
                            .toggleFavorite(
                                city
                            )
                    }
                )

                ForecastBody(
                    hourly = upcomingHours,
                    daily = forecast.daily
                )
            }
        }
    }
}

// =====================================================
// HERO
// =====================================================

@Composable
private fun ForecastHero(
    cityName: String,
    imageResource: Int?,
    condition: WeatherCondition,
    temperature: Double,
    apparentTemperature: Double?,
    humidity: Int?,
    windSpeed: Double?,
    maxTemperature: Double?,
    minTemperature: Double?,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(535.dp)
    ) {

        if (imageResource != null) {

            Image(
                painter = painterResource(
                    id = imageResource
                ),
                contentDescription = cityName,
                modifier =
                    Modifier.fillMaxSize(),
                contentScale =
                    ContentScale.Crop
            )

        } else {

            WeatherBackground(
                condition = condition,
                modifier =
                    Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF06192D)
                                .copy(
                                    alpha = 0.18f
                                ),

                            Color.Transparent,

                            Color(0xFF06192D)
                                .copy(
                                    alpha = 0.25f
                                ),

                            Color(0xFF071B31)
                                .copy(
                                    alpha = 0.95f
                                )
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 24.dp,
                    bottom = 22.dp
                )
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                GlassCircleButton(
                    onClick = onBackClick
                ) {

                    BackArrowIcon()
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                GlassCircleButton(
                    onClick =
                        onFavoriteClick
                ) {

                    HeartIcon(
                        filled = isFavorite
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            Text(
                text = cityName,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = "Türkiye",
                color =
                    Color.White.copy(
                        alpha = 0.74f
                    ),
                fontSize = 15.sp
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "${temperature.toInt()}°",
                color = Color.White,
                fontSize = 78.sp,
                fontWeight =
                    FontWeight.Light
            )

            Text(
                text = condition.label,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            if (
                apparentTemperature != null
            ) {

                Text(
                    text =
                        "Hissedilen ${apparentTemperature.toInt()}°",
                    color =
                        Color.White.copy(
                            alpha = 0.74f
                        ),
                    fontSize = 13.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                if (
                    maxTemperature != null
                ) {

                    Text(
                        text =
                            "↑ ${maxTemperature.toInt()}°",
                        color =
                            Color.White.copy(
                                alpha = 0.80f
                            ),
                        fontSize = 13.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )

                if (
                    minTemperature != null
                ) {

                    Text(
                        text =
                            "↓ ${minTemperature.toInt()}°",
                        color =
                            Color.White.copy(
                                alpha = 0.80f
                            ),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        9.dp
                    )
            ) {

                ForecastMetricCard(
                    title = "Nem",
                    value =
                        if (humidity != null) {
                            "%$humidity"
                        } else {
                            "--"
                        },
                    icon =
                        ForecastMetricIcon.HUMIDITY,
                    modifier =
                        Modifier.weight(1f)
                )

                ForecastMetricCard(
                    title = "Rüzgar",
                    value =
                        if (windSpeed != null) {
                            "${windSpeed.toInt()} km/sa"
                        } else {
                            "--"
                        },
                    icon =
                        ForecastMetricIcon.WIND,
                    modifier =
                        Modifier.weight(1f)
                )

                ForecastMetricCard(
                    title = "Hissedilen",
                    value =
                        if (
                            apparentTemperature != null
                        ) {
                            "${apparentTemperature.toInt()}°"
                        } else {
                            "--"
                        },
                    icon =
                        ForecastMetricIcon.TEMPERATURE,
                    modifier =
                        Modifier.weight(1f)
                )
            }
        }
    }
}

// =====================================================
// GLASS BUTTON
// =====================================================

@Composable
private fun GlassCircleButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                Color(0xFF0B2947)
                    .copy(
                        alpha = 0.55f
                    )
            )
            .clickable {
                onClick()
            },
        contentAlignment =
            Alignment.Center
    ) {

        content()
    }
}

// =====================================================
// METRICS
// =====================================================

private enum class ForecastMetricIcon {
    HUMIDITY,
    WIND,
    TEMPERATURE
}

@Composable
private fun ForecastMetricCard(
    title: String,
    value: String,
    icon: ForecastMetricIcon,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .height(78.dp)
            .clip(
                RoundedCornerShape(
                    20.dp
                )
            )
            .background(
                Color(0xFF0A2949)
                    .copy(
                        alpha = 0.66f
                    )
            )
            .padding(
                horizontal = 7.dp,
                vertical = 10.dp
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize(),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                MetricCanvasIcon(
                    type = icon
                )

                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )

                Text(
                    text = value,
                    color = Color.White,
                    fontSize =
                        if (
                            value.length > 7
                        ) {
                            12.sp
                        } else {
                            16.sp
                        },
                    fontWeight =
                        FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text = title,
                color =
                    Color.White.copy(
                        alpha = 0.63f
                    ),
                fontSize = 10.sp
            )
        }
    }
}

// =====================================================
// BODY
// =====================================================

@Composable
private fun ForecastBody(
    hourly: List<HourlyForecast>,
    daily: List<DailyForecast>
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD7EAFD),
                        Color(0xFFE9F4FE),
                        Color(0xFFF4F8FC)
                    )
                )
            )
            .padding(
                top = 25.dp,
                bottom = 36.dp
            )
    ) {

        ForecastSectionHeader(
            title = "Saatlik Tahmin",
            subtitle =
                "Önümüzdeki saatler"
        )

        Spacer(
            modifier =
                Modifier.height(13.dp)
        )

        ForecastHourlyRow(
            hourly = hourly
        )

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )

        ForecastSectionHeader(
            title = "Yağış İhtimali",
            subtitle =
                "Önümüzdeki 12 saat"
        )

        Spacer(
            modifier =
                Modifier.height(13.dp)
        )

        PrecipitationChart(
            hourly = hourly
        )

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )

        ForecastSectionHeader(
            title = "7 Günlük Tahmin",
            subtitle =
                "Haftalık hava görünümü"
        )

        Spacer(
            modifier =
                Modifier.height(13.dp)
        )

        DailyForecastPanel(
            daily = daily
        )
    }
}

// =====================================================
// SECTION HEADER
// =====================================================

@Composable
private fun ForecastSectionHeader(
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(38.dp)
                .clip(CircleShape)
                .background(
                    Color(0xFF2477CC)
                )
        )

        Spacer(
            modifier =
                Modifier.width(10.dp)
        )

        Column {

            Text(
                text = title,
                color =
                    Color(0xFF10233F),
                fontSize = 21.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = subtitle,
                color =
                    Color(0xFF6B8099),
                fontSize = 11.sp
            )
        }
    }
}

// =====================================================
// HOURLY
// =====================================================

@Composable
private fun ForecastHourlyRow(
    hourly: List<HourlyForecast>
) {

    LazyRow(
        contentPadding =
            PaddingValues(
                horizontal = 20.dp
            ),
        horizontalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {

        itemsIndexed(
            hourly
        ) { index, item ->

            ForecastHourlyCard(
                forecast = item,
                isNow = index == 0
            )
        }
    }
}

@Composable
private fun ForecastHourlyCard(
    forecast: HourlyForecast,
    isNow: Boolean
) {

    val condition =
        WeatherCondition
            .fromWeatherCode(
                forecast.weatherCode
            )

    val background =
        if (isNow) {

            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2477CC),
                    Color(0xFF175A9D)
                )
            )

        } else {

            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(
                        alpha = 0.96f
                    ),
                    Color(0xFFF4F9FE)
                )
            )
        }

    val mainText =
        if (isNow) {
            Color.White
        } else {
            Color(0xFF10233F)
        }

    val secondaryText =
        if (isNow) {
            Color.White.copy(
                alpha = 0.66f
            )
        } else {
            Color(0xFF71849C)
        }

    Box(
        modifier = Modifier
            .width(86.dp)
            .height(157.dp)
            .clip(
                RoundedCornerShape(
                    24.dp
                )
            )
            .background(background)
            .padding(
                vertical = 13.dp
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize(),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    if (isNow) {
                        "Şimdi"
                    } else {
                        forecast.time.format(
                            DateTimeFormatter
                                .ofPattern(
                                    "HH:mm"
                                )
                        )
                    },
                color = mainText,
                fontSize = 11.sp,
                fontWeight =
                    if (isNow) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            ForecastWeatherIcon(
                condition = condition,
                modifier =
                    Modifier.size(36.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "${forecast.temperatureC.toInt()}°",
                color = mainText,
                fontSize = 19.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                MiniRainDrop(
                    color =
                        if (isNow) {
                            Color(0xFFC8E9FF)
                        } else {
                            Color(0xFF368FE7)
                        }
                )

                Spacer(
                    modifier =
                        Modifier.width(3.dp)
                )

                Text(
                    text =
                        forecast
                            .precipitationProbability
                            ?.let {
                                "%$it"
                            }
                            ?: "--",
                    color =
                        secondaryText,
                    fontSize = 10.sp,
                    fontWeight =
                        FontWeight.Medium
                )
            }
        }
    }
}

// =====================================================
// PRECIPITATION GRAPH
// =====================================================

@Composable
private fun PrecipitationChart(
    hourly: List<HourlyForecast>
) {

    val chartData =
        hourly
            .take(12)
            .map { forecast ->

                forecast to
                        (
                                forecast
                                    .precipitationProbability
                                    ?: 0
                                )
            }

    Card(
        modifier = Modifier
            .padding(
                horizontal = 20.dp
            )
            .fillMaxWidth(),
        shape =
            RoundedCornerShape(
                28.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.92f
                    )
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 17.dp
                )
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFFE4F3FF)
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    MiniRainDrop(
                        color =
                            Color(0xFF2788E5)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = "Yağış Tahmini",
                        fontSize = 15.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            Color(0xFF10233F)
                    )

                    Text(
                        text =
                            precipitationSummary(
                                chartData.map {
                                    it.second
                                }
                            ),
                        fontSize = 11.sp,
                        color =
                            Color(0xFF71849C)
                    )
                }

                val highestChance =
                    chartData
                        .maxOfOrNull {
                            it.second
                        }
                        ?: 0

                Text(
                    text =
                        "%$highestChance",
                    fontSize = 22.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        Color(0xFF2477CC)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            if (
                chartData.isEmpty()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "Yağış verisi bulunamadı",
                        fontSize = 12.sp,
                        color =
                            Color(0xFF71849C)
                    )
                }

            } else {

                PrecipitationChartCanvas(
                    probabilities =
                        chartData.map {
                            it.second
                        }
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                PrecipitationTimeLabels(
                    hourly =
                        chartData.map {
                            it.first
                        }
                )
            }

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFF2E8FE8)
                        )
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text =
                        "Saatlik yağış olasılığı",
                    fontSize = 10.sp,
                    color =
                        Color(0xFF71849C)
                )

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Text(
                    text = "0 – 100%",
                    fontSize = 10.sp,
                    fontWeight =
                        FontWeight.Medium,
                    color =
                        Color(0xFF71849C)
                )
            }
        }
    }
}

@Composable
private fun PrecipitationChartCanvas(
    probabilities: List<Int>
) {

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
    ) {

        if (
            probabilities.isEmpty()
        ) {
            return@Canvas
        }

        val leftPadding =
            8.dp.toPx()

        val rightPadding =
            8.dp.toPx()

        val topPadding =
            22.dp.toPx()

        val bottomPadding =
            16.dp.toPx()

        val chartWidth =
            size.width -
                    leftPadding -
                    rightPadding

        val chartHeight =
            size.height -
                    topPadding -
                    bottomPadding

        listOf(
            0f,
            0.25f,
            0.50f,
            0.75f,
            1f
        ).forEach { ratio ->

            val y =
                topPadding +
                        chartHeight *
                        (
                                1f -
                                        ratio
                                )

            drawLine(
                color =
                    Color(0xFFB9D1E8)
                        .copy(
                            alpha = 0.30f
                        ),
                start = Offset(
                    leftPadding,
                    y
                ),
                end = Offset(
                    size.width -
                            rightPadding,
                    y
                ),
                strokeWidth =
                    1.dp.toPx()
            )
        }

        val pointSpacing =
            if (
                probabilities.size > 1
            ) {

                chartWidth /
                        (
                                probabilities.size -
                                        1
                                )

            } else {

                0f
            }

        val points =
            probabilities
                .mapIndexed {
                        index,
                        probability ->

                    val x =
                        leftPadding +
                                pointSpacing *
                                index

                    val normalized =
                        probability
                            .coerceIn(
                                0,
                                100
                            ) / 100f

                    val y =
                        topPadding +
                                chartHeight *
                                (
                                        1f -
                                                normalized
                                        )

                    Offset(
                        x = x,
                        y = y
                    )
                }

        val areaPath =
            Path().apply {

                moveTo(
                    points.first().x,
                    topPadding +
                            chartHeight
                )

                lineTo(
                    points.first().x,
                    points.first().y
                )

                if (
                    points.size > 1
                ) {

                    for (
                    index in
                    0 until
                            points.lastIndex
                    ) {

                        val current =
                            points[index]

                        val next =
                            points[
                                index + 1
                            ]

                        val middleX =
                            (
                                    current.x +
                                            next.x
                                    ) / 2f

                        cubicTo(
                            middleX,
                            current.y,
                            middleX,
                            next.y,
                            next.x,
                            next.y
                        )
                    }
                }

                lineTo(
                    points.last().x,
                    topPadding +
                            chartHeight
                )

                close()
            }

        drawPath(
            path = areaPath,
            brush =
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3898ED)
                            .copy(
                                alpha = 0.30f
                            ),

                        Color(0xFF3898ED)
                            .copy(
                                alpha = 0.03f
                            )
                    ),
                    startY =
                        topPadding,
                    endY =
                        topPadding +
                                chartHeight
                )
        )

        val linePath =
            Path().apply {

                moveTo(
                    points.first().x,
                    points.first().y
                )

                if (
                    points.size > 1
                ) {

                    for (
                    index in
                    0 until
                            points.lastIndex
                    ) {

                        val current =
                            points[index]

                        val next =
                            points[
                                index + 1
                            ]

                        val middleX =
                            (
                                    current.x +
                                            next.x
                                    ) / 2f

                        cubicTo(
                            middleX,
                            current.y,
                            middleX,
                            next.y,
                            next.x,
                            next.y
                        )
                    }
                }
            }

        drawPath(
            path = linePath,
            color =
                Color(0xFF2788E5),
            style = Stroke(
                width =
                    3.dp.toPx()
            )
        )

        points.forEach { point ->

            drawCircle(
                color = Color.White,
                radius =
                    5.dp.toPx(),
                center = point
            )

            drawCircle(
                color =
                    Color(0xFF2788E5),
                radius =
                    3.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
private fun PrecipitationTimeLabels(
    hourly: List<HourlyForecast>
) {

    if (
        hourly.isEmpty()
    ) {
        return
    }

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        hourly.forEachIndexed {
                index,
                forecast ->

            Box(
                modifier =
                    Modifier.weight(1f),
                contentAlignment =
                    Alignment.Center
            ) {

                if (
                    index % 2 == 0 ||
                    index ==
                    hourly.lastIndex
                ) {

                    Text(
                        text =
                            if (index == 0) {

                                "Şimdi"

                            } else {

                                forecast.time
                                    .format(
                                        DateTimeFormatter
                                            .ofPattern(
                                                "HH:mm"
                                            )
                                    )
                            },
                        fontSize = 8.sp,
                        color =
                            Color(0xFF71849C),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun precipitationSummary(
    probabilities: List<Int>
): String {

    if (
        probabilities.isEmpty()
    ) {
        return "Yağış verisi bulunamadı"
    }

    val highest =
        probabilities
            .maxOrNull()
            ?: 0

    return when {

        highest < 20 ->
            "Yağış ihtimali oldukça düşük"

        highest < 40 ->
            "Düşük yağış ihtimali var"

        highest < 60 ->
            "Yağış ihtimali orta seviyede"

        highest < 80 ->
            "Yağış ihtimali yüksek"

        else ->
            "Kuvvetli yağış ihtimali var"
    }
}

// =====================================================
// DAILY
// =====================================================

@Composable
private fun DailyForecastPanel(
    daily: List<DailyForecast>
) {

    Card(
        modifier = Modifier
            .padding(
                horizontal = 20.dp
            )
            .fillMaxWidth(),
        shape =
            RoundedCornerShape(
                28.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White.copy(
                        alpha = 0.88f
                    )
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 7.dp
                )
        ) {

            val visibleDays =
                daily.take(7)

            visibleDays
                .forEachIndexed {
                        index,
                        day ->

                    DailyForecastRow(
                        day = day,
                        isToday =
                            index == 0
                    )

                    if (
                        index !=
                        visibleDays.lastIndex
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(
                                    Color(0xFF173657)
                                        .copy(
                                            alpha = 0.07f
                                        )
                                )
                        )
                    }
                }
        }
    }
}

@Composable
private fun DailyForecastRow(
    day: DailyForecast,
    isToday: Boolean
) {

    val condition =
        WeatherCondition
            .fromWeatherCode(
                day.weatherCode
            )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                if (isToday) {
                    "Bugün"
                } else {
                    formatTurkishDay(
                        day.date
                    )
                },
            modifier =
                Modifier.width(93.dp),
            color =
                Color(0xFF10233F),
            fontSize = 13.sp,
            fontWeight =
                if (isToday) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
        )

        ForecastWeatherIcon(
            condition = condition,
            modifier =
                Modifier.size(31.dp)
        )

        Spacer(
            modifier =
                Modifier.width(7.dp)
        )

        Text(
            text =
                day.precipitationProbability
                    ?.let {
                        "%$it"
                    }
                    ?: "--",
            modifier =
                Modifier.width(37.dp),
            color =
                Color(0xFF328BE3),
            fontSize = 9.sp,
            fontWeight =
                FontWeight.SemiBold
        )

        Text(
            text =
                "${day.minTemperatureC.toInt()}°",
            color =
                Color(0xFF75869A),
            fontSize = 12.sp
        )

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        TemperatureBar(
            minTemperature =
                day.minTemperatureC,
            maxTemperature =
                day.maxTemperatureC,
            modifier =
                Modifier.weight(1f)
        )

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        Text(
            text =
                "${day.maxTemperatureC.toInt()}°",
            color =
                Color(0xFF10233F),
            fontSize = 12.sp,
            fontWeight =
                FontWeight.Bold
        )
    }
}

private fun formatTurkishDay(
    date: LocalDate
): String {

    val locale =
        Locale(
            "tr",
            "TR"
        )

    val dayName =
        date.format(
            DateTimeFormatter
                .ofPattern(
                    "EEEE",
                    locale
                )
        )

    return dayName
        .replaceFirstChar {

            if (
                it.isLowerCase()
            ) {
                it.titlecase(
                    locale
                )
            } else {
                it.toString()
            }
        }
}

// =====================================================
// TEMPERATURE BAR
// =====================================================

@Composable
private fun TemperatureBar(
    minTemperature: Double,
    maxTemperature: Double,
    modifier: Modifier = Modifier
) {

    val difference =
        (
                maxTemperature -
                        minTemperature
                )
            .coerceAtLeast(
                1.0
            )

    val progress =
        (
                difference /
                        20.0
                )
            .coerceIn(
                0.25,
                1.0
            )
            .toFloat()

    Box(
        modifier = modifier
            .height(5.dp)
            .clip(CircleShape)
            .background(
                Color(0xFFD7E4EF)
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(
                    progress
                )
                .height(5.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4DA8ED),
                            Color(0xFFFFC84D)
                        )
                    )
                )
        )
    }
}

// =====================================================
// WEATHER ICON
// =====================================================

@Composable
private fun ForecastWeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
    ) {

        val sun =
            Color(0xFFFFC83D)

        val whiteCloud =
            Color(0xFFEAF3FB)

        val cloud =
            Color(0xFF8EA9C4)

        val rain =
            Color(0xFF398FE8)

        when (condition) {

            WeatherCondition.CLEAR -> {

                drawForecastSun(
                    color = sun
                )
            }

            WeatherCondition.MAINLY_CLEAR,
            WeatherCondition.PARTLY_CLOUDY -> {

                drawCircle(
                    color = sun,
                    radius =
                        size.width * 0.20f,
                    center = Offset(
                        size.width * 0.34f,
                        size.height * 0.33f
                    )
                )

                drawForecastCloud(
                    color = whiteCloud,
                    yOffset =
                        size.height * 0.09f
                )
            }

            WeatherCondition.RAIN,
            WeatherCondition.RAIN_SHOWERS,
            WeatherCondition.DRIZZLE -> {

                drawForecastCloud(
                    color = cloud
                )

                repeat(3) { index ->

                    val x =
                        size.width *
                                (
                                        0.30f +
                                                index *
                                                0.20f
                                        )

                    drawLine(
                        color = rain,
                        start = Offset(
                            x,
                            size.height *
                                    0.68f
                        ),
                        end = Offset(
                            x -
                                    size.width *
                                    0.04f,
                            size.height *
                                    0.88f
                        ),
                        strokeWidth =
                            size.width *
                                    0.045f
                    )
                }
            }

            WeatherCondition.SNOW,
            WeatherCondition.SNOW_SHOWERS -> {

                drawForecastCloud(
                    color = cloud
                )

                repeat(3) { index ->

                    drawCircle(
                        color =
                            Color(0xFF68AFF3),
                        radius =
                            size.width *
                                    0.04f,
                        center = Offset(
                            size.width *
                                    (
                                            0.30f +
                                                    index *
                                                    0.20f
                                            ),
                            size.height *
                                    0.82f
                        )
                    )
                }
            }

            WeatherCondition.THUNDERSTORM -> {

                drawForecastCloud(
                    color =
                        Color(0xFF738AA2)
                )

                val bolt =
                    Path()

                bolt.moveTo(
                    size.width * 0.53f,
                    size.height * 0.62f
                )

                bolt.lineTo(
                    size.width * 0.41f,
                    size.height * 0.78f
                )

                bolt.lineTo(
                    size.width * 0.52f,
                    size.height * 0.78f
                )

                bolt.lineTo(
                    size.width * 0.44f,
                    size.height * 0.95f
                )

                drawPath(
                    path = bolt,
                    color =
                        Color(0xFFFFD340),
                    style = Stroke(
                        width =
                            size.width *
                                    0.06f
                    )
                )
            }

            else -> {

                drawForecastCloud(
                    color = cloud
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawForecastSun(
    color: Color
) {

    val center =
        Offset(
            size.width / 2f,
            size.height / 2f
        )

    drawCircle(
        color = color,
        radius =
            size.width * 0.23f,
        center = center
    )

    repeat(8) { index ->

        val angle =
            Math.toRadians(
                (
                        index *
                                45
                        ).toDouble()
            )

        val cos =
            kotlin.math
                .cos(angle)
                .toFloat()

        val sin =
            kotlin.math
                .sin(angle)
                .toFloat()

        drawLine(
            color = color,
            start = Offset(
                center.x +
                        cos *
                        size.width *
                        0.33f,

                center.y +
                        sin *
                        size.width *
                        0.33f
            ),
            end = Offset(
                center.x +
                        cos *
                        size.width *
                        0.44f,

                center.y +
                        sin *
                        size.width *
                        0.44f
            ),
            strokeWidth =
                size.width *
                        0.035f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawForecastCloud(
    color: Color,
    yOffset: Float = 0f
) {

    drawCircle(
        color = color,
        radius =
            size.width * 0.19f,
        center = Offset(
            size.width * 0.34f,
            size.height * 0.49f +
                    yOffset
        )
    )

    drawCircle(
        color = color,
        radius =
            size.width * 0.26f,
        center = Offset(
            size.width * 0.55f,
            size.height * 0.43f +
                    yOffset
        )
    )

    drawCircle(
        color = color,
        radius =
            size.width * 0.17f,
        center = Offset(
            size.width * 0.73f,
            size.height * 0.52f +
                    yOffset
        )
    )

    drawRoundRect(
        color = color,
        topLeft = Offset(
            size.width * 0.18f,
            size.height * 0.49f +
                    yOffset
        ),
        size = Size(
            size.width * 0.68f,
            size.height * 0.23f
        ),
        cornerRadius =
            CornerRadius(
                size.width * 0.12f,
                size.width * 0.12f
            )
    )
}

// =====================================================
// MINI RAIN DROP
// =====================================================

@Composable
private fun MiniRainDrop(
    color: Color
) {

    Canvas(
        modifier =
            Modifier.size(11.dp)
    ) {

        val path =
            Path()

        path.moveTo(
            size.width * 0.50f,
            size.height * 0.05f
        )

        path.cubicTo(
            size.width * 0.20f,
            size.height * 0.40f,
            size.width * 0.20f,
            size.height * 0.70f,
            size.width * 0.50f,
            size.height * 0.91f
        )

        path.cubicTo(
            size.width * 0.80f,
            size.height * 0.70f,
            size.width * 0.80f,
            size.height * 0.40f,
            size.width * 0.50f,
            size.height * 0.05f
        )

        drawPath(
            path = path,
            color = color
        )
    }
}

// =====================================================
// METRIC ICONS
// =====================================================

@Composable
private fun MetricCanvasIcon(
    type: ForecastMetricIcon
) {

    Canvas(
        modifier =
            Modifier.size(19.dp)
    ) {

        when (type) {

            ForecastMetricIcon.HUMIDITY -> {

                val path =
                    Path()

                path.moveTo(
                    size.width * 0.50f,
                    size.height * 0.06f
                )

                path.cubicTo(
                    size.width * 0.20f,
                    size.height * 0.40f,
                    size.width * 0.20f,
                    size.height * 0.70f,
                    size.width * 0.50f,
                    size.height * 0.91f
                )

                path.cubicTo(
                    size.width * 0.80f,
                    size.height * 0.70f,
                    size.width * 0.80f,
                    size.height * 0.40f,
                    size.width * 0.50f,
                    size.height * 0.06f
                )

                drawPath(
                    path = path,
                    color =
                        Color(0xFFC4E8FF),
                    style = Stroke(
                        width =
                            1.6.dp.toPx()
                    )
                )
            }

            ForecastMetricIcon.WIND -> {

                repeat(3) { index ->

                    val y =
                        size.height *
                                (
                                        0.27f +
                                                index *
                                                0.23f
                                        )

                    drawLine(
                        color =
                            Color(0xFFC4E8FF),
                        start = Offset(
                            size.width *
                                    0.08f,
                            y
                        ),
                        end = Offset(
                            size.width *
                                    (
                                            0.72f +
                                                    index *
                                                    0.06f
                                            ),
                            y
                        ),
                        strokeWidth =
                            1.5.dp.toPx()
                    )
                }
            }

            ForecastMetricIcon.TEMPERATURE -> {

                drawCircle(
                    color =
                        Color(0xFFFFB07A),
                    radius =
                        size.width * 0.18f,
                    center = Offset(
                        size.width * 0.50f,
                        size.height * 0.76f
                    )
                )

                drawRoundRect(
                    color =
                        Color(0xFFFFB07A),
                    topLeft = Offset(
                        size.width * 0.43f,
                        size.height * 0.10f
                    ),
                    size = Size(
                        size.width * 0.14f,
                        size.height * 0.66f
                    ),
                    cornerRadius =
                        CornerRadius(
                            20f,
                            20f
                        )
                )
            }
        }
    }
}

// =====================================================
// BACK
// =====================================================

@Composable
private fun BackArrowIcon() {

    Canvas(
        modifier =
            Modifier.size(20.dp)
    ) {

        val path =
            Path()

        path.moveTo(
            size.width * 0.67f,
            size.height * 0.16f
        )

        path.lineTo(
            size.width * 0.32f,
            size.height * 0.50f
        )

        path.lineTo(
            size.width * 0.67f,
            size.height * 0.84f
        )

        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(
                width =
                    2.dp.toPx()
            )
        )
    }
}

// =====================================================
// HEART
// =====================================================

@Composable
private fun HeartIcon(
    filled: Boolean
) {

    Canvas(
        modifier =
            Modifier.size(21.dp)
    ) {

        val path =
            Path()

        path.moveTo(
            size.width * 0.50f,
            size.height * 0.86f
        )

        path.cubicTo(
            size.width * 0.40f,
            size.height * 0.76f,
            size.width * 0.10f,
            size.height * 0.55f,
            size.width * 0.10f,
            size.height * 0.31f
        )

        path.cubicTo(
            size.width * 0.10f,
            size.height * 0.12f,
            size.width * 0.34f,
            size.height * 0.08f,
            size.width * 0.50f,
            size.height * 0.27f
        )

        path.cubicTo(
            size.width * 0.66f,
            size.height * 0.08f,
            size.width * 0.90f,
            size.height * 0.12f,
            size.width * 0.90f,
            size.height * 0.31f
        )

        path.cubicTo(
            size.width * 0.90f,
            size.height * 0.55f,
            size.width * 0.60f,
            size.height * 0.76f,
            size.width * 0.50f,
            size.height * 0.86f
        )

        if (filled) {

            drawPath(
                path = path,
                color =
                    Color(0xFFFF4F68)
            )

        } else {

            drawPath(
                path = path,
                color = Color.White,
                style = Stroke(
                    width =
                        1.8.dp.toPx()
                )
            )
        }
    }
}

// =====================================================
// CITY IMAGE
// =====================================================

private fun forecastCityImageResource(
    cityName: String
): Int? {

    val normalizedCityName =
        cityName
            .trim()
            .lowercase(
                Locale(
                    "tr",
                    "TR"
                )
            )

    return when (
        normalizedCityName
    ) {

        "istanbul" ->
            R.drawable.city_istanbul

        "ankara" ->
            R.drawable.city_ankara

        "izmir" ->
            R.drawable.city_izmir

        "bursa" ->
            R.drawable.city_bursa

        else ->
            null
    }
}

// =====================================================
// LOADING
// =====================================================

@Composable
private fun ForecastLoadingScreen(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B3864),
                        Color(0xFF3B8ED4)
                    )
                )
            ),
        contentAlignment =
            Alignment.Center
    ) {

        CircularProgressIndicator(
            color = Color.White
        )
    }
}

// =====================================================
// ERROR
// =====================================================

@Composable
private fun ForecastErrorScreen(
    message: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Color(0xFF0B3864)
            )
    ) {

        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(
                        alpha = 0.14f
                    )
                )
                .clickable {
                    onBackClick()
                },
            contentAlignment =
                Alignment.Center
        ) {

            BackArrowIcon()
        }

        Column(
            modifier =
                Modifier.align(
                    Alignment.Center
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    "Hava durumu yüklenemedi",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text = message,
                color =
                    Color.White.copy(
                        alpha = 0.70f
                    ),
                fontSize = 12.sp
            )
        }
    }
}