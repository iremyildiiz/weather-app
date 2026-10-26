package com.example.havadurumu.ui

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havadurumu.ForecastViewModel
import com.example.havadurumu.R
import com.example.havadurumu.model.City
import com.example.havadurumu.model.HourlyForecast
import com.example.havadurumu.model.WeatherCondition
import com.example.havadurumu.ui.component.WeatherBackground
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    city: City,
    viewModel: ForecastViewModel,
    onSearchClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.forecastState.collectAsState()

    LaunchedEffect(city.latitude, city.longitude) {
        viewModel.loadForecast(
            latitude = city.latitude,
            longitude = city.longitude
        )
    }

    when (val currentState = state) {
        LoadState.Idle,
        LoadState.Loading -> {
            LoadingScreen(modifier)
        }

        is LoadState.Error -> {
            ErrorScreen(
                message = currentState.message,
                modifier = modifier
            )
        }

        is LoadState.Success -> {
            val forecast = currentState.data

            val condition =
                WeatherCondition.fromWeatherCode(
                    forecast.current.weatherCode
                )

            val today = forecast.daily.firstOrNull()

            val upcomingHours =
                forecast.hourly
                    .filter {
                        !it.time.isBefore(
                            forecast.current.observedAt
                        )
                    }
                    .take(10)

            val currentRainChance =
                upcomingHours.firstOrNull()
                    ?.precipitationProbability

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFEAF4FF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    HomeHero(
                        city = city,
                        temperature = forecast.current.temperatureC,
                        condition = condition,
                        maxTemperature = today?.maxTemperatureC,
                        minTemperature = today?.minTemperatureC,
                        apparentTemperature =
                            forecast.current.apparentTemperatureC,
                        humidity =
                            forecast.current.humidityPercent,
                        windSpeed =
                            forecast.current.windSpeedKmh,
                        onSearchClick = onSearchClick
                    )

                    WeatherContent(
                        condition = condition,
                        temperature =
                            forecast.current.temperatureC,
                        apparentTemperature =
                            forecast.current.apparentTemperatureC,
                        humidity =
                            forecast.current.humidityPercent,
                        windSpeed =
                            forecast.current.windSpeedKmh,
                        rainChance = currentRainChance,
                        maxTemperature =
                            today?.maxTemperatureC,
                        minTemperature =
                            today?.minTemperatureC,
                        upcomingHours = upcomingHours,
                        onDetailClick = onDetailClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHero(
    city: City,
    temperature: Double,
    condition: WeatherCondition,
    maxTemperature: Double?,
    minTemperature: Double?,
    apparentTemperature: Double?,
    humidity: Int?,
    windSpeed: Double?,
    onSearchClick: () -> Unit
) {
    val imageResource = cityImageResource(city.name)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        if (imageResource != null) {
            Image(
                painter = painterResource(imageResource),
                contentDescription = city.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            WeatherBackground(
                condition = condition,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF06182F).copy(alpha = 0.18f),
                            Color.Transparent,
                            Color(0xFF07192F).copy(alpha = 0.30f),
                            Color(0xFF0A2748).copy(alpha = 0.90f)
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
                    top = 36.dp,
                    bottom = 20.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            Color(0xFF092847)
                                .copy(alpha = 0.62f)
                        )
                        .clickable {
                            onSearchClick()
                        }
                        .padding(
                            horizontal = 15.dp,
                            vertical = 9.dp
                        )
                ) {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        LocationPin()

                        Spacer(Modifier.width(7.dp))

                        Text(
                            text = city.name,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.width(7.dp))

                        Text(
                            text = "⌄",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFF092847)
                                .copy(alpha = 0.55f)
                        )
                        .clickable {
                            onSearchClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    SearchIcon(Color.White)
                }
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "${temperature.toInt()}°",
                color = Color.White,
                fontSize = 84.sp,
                fontWeight = FontWeight.Light
            )

            Text(
                text = condition.label,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(6.dp))

            Row {
                if (maxTemperature != null) {
                    Text(
                        text = "↑ ${maxTemperature.toInt()}°",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.width(14.dp))

                if (minTemperature != null) {
                    Text(
                        text = "↓ ${minTemperature.toInt()}°",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 14.sp
                    )
                }

                if (apparentTemperature != null) {
                    Spacer(Modifier.width(14.dp))

                    Text(
                        text =
                            "Hissedilen ${apparentTemperature.toInt()}°",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                HeroInfo(
                    label = "Nem",
                    value = humidity?.let { "%$it" } ?: "--",
                    modifier = Modifier.weight(1f)
                )

                HeroInfo(
                    label = "Rüzgar",
                    value =
                        windSpeed?.let {
                            "${it.toInt()} km/s"
                        } ?: "--",
                    modifier = Modifier.weight(1f)
                )

                HeroInfo(
                    label = "Hava",
                    value = condition.label,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeroInfo(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Color(0xFF0A2949).copy(alpha = 0.60f)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 13.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize =
                if (value.length > 10) 12.sp else 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.67f),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun WeatherContent(
    condition: WeatherCondition,
    temperature: Double,
    apparentTemperature: Double?,
    humidity: Int?,
    windSpeed: Double?,
    rainChance: Int?,
    maxTemperature: Double?,
    minTemperature: Double?,
    upcomingHours: List<HourlyForecast>,
    onDetailClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDCEEFF),
                        Color(0xFFEAF4FF),
                        Color(0xFFF2F7FD)
                    )
                )
            )
            .padding(
                top = 22.dp,
                bottom = 36.dp
            )
    ) {
        TodaySummaryCard(
            condition = condition,
            temperature = temperature,
            maxTemperature = maxTemperature,
            minTemperature = minTemperature,
            rainChance = rainChance
        )

        Spacer(Modifier.height(28.dp))

        SectionHeader(
            title = "Önümüzdeki Saatler",
            action = "Detaylar",
            onClick = onDetailClick
        )

        Spacer(Modifier.height(12.dp))

        HourlySection(
            hourly = upcomingHours
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Bugün ne bekleniyor?",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10233F),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        WeatherDetailsPanel(
            condition = condition,
            apparentTemperature = apparentTemperature,
            humidity = humidity,
            windSpeed = windSpeed,
            rainChance = rainChance
        )

        Spacer(Modifier.height(28.dp))

        ForecastButton(
            onClick = onDetailClick
        )
    }
}

@Composable
private fun TodaySummaryCard(
    condition: WeatherCondition,
    temperature: Double,
    maxTemperature: Double?,
    minTemperature: Double?,
    rainChance: Int?
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1D65B5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIcon(
                condition = condition,
                modifier = Modifier.size(58.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Bugün",
                    color = Color.White.copy(alpha = 0.70f),
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = condition.label,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = buildString {
                        append("${temperature.toInt()}°")

                        if (
                            maxTemperature != null &&
                            minTemperature != null
                        ) {
                            append(
                                "  •  ${maxTemperature.toInt()}° / ${minTemperature.toInt()}°"
                            )
                        }
                    },
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 13.sp
                )
            }

            if (rainChance != null) {
                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {
                    RainDropIcon(
                        color = Color(0xFFBDE1FF)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "%$rainChance",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "yağış",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = Color(0xFF10233F),
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = action,
            color = Color(0xFF246CC0),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable {
                onClick()
            }
        )
    }
}

@Composable
private fun HourlySection(
    hourly: List<HourlyForecast>
) {
    LazyRow(
        contentPadding =
            PaddingValues(horizontal = 20.dp),
        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(hourly) { index, forecast ->
            HourCard(
                forecast = forecast,
                isNow = index == 0
            )
        }
    }
}

@Composable
private fun HourCard(
    forecast: HourlyForecast,
    isNow: Boolean
) {
    val condition =
        WeatherCondition.fromWeatherCode(
            forecast.weatherCode
        )

    val background =
        if (isNow) {
            Color(0xFF246CC0)
        } else {
            Color.White.copy(alpha = 0.82f)
        }

    val mainText =
        if (isNow) Color.White
        else Color(0xFF10233F)

    val secondaryText =
        if (isNow) {
            Color.White.copy(alpha = 0.72f)
        } else {
            Color(0xFF71839B)
        }

    Card(
        modifier = Modifier
            .width(86.dp)
            .height(154.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation =
                if (isNow) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 13.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text =
                    if (isNow) {
                        "Şimdi"
                    } else {
                        forecast.time.format(
                            DateTimeFormatter.ofPattern(
                                "HH:mm"
                            )
                        )
                    },
                color = mainText,
                fontSize = 12.sp,
                fontWeight =
                    if (isNow) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
            )

            Spacer(Modifier.height(10.dp))

            WeatherIcon(
                condition = condition,
                modifier = Modifier.size(35.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text =
                    "${forecast.temperatureC.toInt()}°",
                color = mainText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(7.dp))

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                RainDropIcon(
                    color =
                        if (isNow) {
                            Color(0xFFBDE1FF)
                        } else {
                            Color(0xFF398EE8)
                        },
                    modifier = Modifier.size(11.dp)
                )

                Spacer(Modifier.width(3.dp))

                Text(
                    text =
                        forecast
                            .precipitationProbability
                            ?.let { "%$it" }
                            ?: "--",
                    color = secondaryText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailsPanel(
    condition: WeatherCondition,
    apparentTemperature: Double?,
    humidity: Int?,
    windSpeed: Double?,
    rainChance: Int?
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                Color(0xFFD7EAFD).copy(alpha = 0.90f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherIcon(
                    condition = condition,
                    modifier = Modifier.size(42.dp)
                )

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = condition.label,
                        color = Color(0xFF10233F),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = weatherMessage(
                            condition = condition,
                            rainChance = rainChance
                        ),
                        color = Color(0xFF607893),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                DetailTile(
                    title = "Yağış",
                    value =
                        rainChance?.let { "%$it" } ?: "--",
                    type = DetailIconType.RAIN,
                    modifier = Modifier.weight(1f)
                )

                DetailTile(
                    title = "Rüzgar",
                    value =
                        windSpeed?.let {
                            "${it.toInt()} km/s"
                        } ?: "--",
                    type = DetailIconType.WIND,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                DetailTile(
                    title = "Hissedilen",
                    value =
                        apparentTemperature?.let {
                            "${it.toInt()}°"
                        } ?: "--",
                    type = DetailIconType.TEMP,
                    modifier = Modifier.weight(1f)
                )

                DetailTile(
                    title = "Nem",
                    value =
                        humidity?.let { "%$it" } ?: "--",
                    type = DetailIconType.HUMIDITY,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private enum class DetailIconType {
    RAIN,
    WIND,
    TEMP,
    HUMIDITY
}

@Composable
private fun DetailTile(
    title: String,
    value: String,
    type: DetailIconType,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(75.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(
                Color.White.copy(alpha = 0.72f)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailIcon(type)

        Spacer(Modifier.width(9.dp))

        Column {
            Text(
                text = title,
                color = Color(0xFF70839A),
                fontSize = 10.sp
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = value,
                color = Color(0xFF10233F),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ForecastButton(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF163F70)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 17.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CalendarIcon()

            Spacer(Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "7 Günlük Tahmin",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text =
                        "Önümüzdeki günlerin hava durumunu gör",
                    color =
                        Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp
                )
            }

            Text(
                text = "›",
                color = Color.White,
                fontSize = 28.sp
            )
        }
    }
}

private fun weatherMessage(
    condition: WeatherCondition,
    rainChance: Int?
): String {
    return when {
        rainChance != null && rainChance >= 70 ->
            "Yağış ihtimali yüksek, hazırlıklı ol."

        rainChance != null && rainChance >= 40 ->
            "Gün içinde yağış görülebilir."

        condition == WeatherCondition.CLEAR ->
            "Açık ve sakin bir hava bekleniyor."

        condition == WeatherCondition.MAINLY_CLEAR ->
            "Genel olarak açık bir hava bekleniyor."

        condition == WeatherCondition.PARTLY_CLOUDY ->
            "Gün içinde parçalı bulutlar görülebilir."

        condition == WeatherCondition.OVERCAST ->
            "Bugün gökyüzü çoğunlukla kapalı."

        condition == WeatherCondition.FOG ->
            "Sis nedeniyle görüş mesafesi azalabilir."

        condition == WeatherCondition.SNOW ||
                condition == WeatherCondition.SNOW_SHOWERS ->
            "Gün içinde kar yağışı görülebilir."

        condition == WeatherCondition.THUNDERSTORM ->
            "Gök gürültülü hava bekleniyor."

        else ->
            "Günün hava koşullarını takip edebilirsin."
    }
}

@Composable
private fun WeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
    ) {
        val sunColor = Color(0xFFFFC83D)
        val cloudColor = Color(0xFFEAF3FB)
        val darkCloud = Color(0xFF8CA9C6)
        val rainColor = Color(0xFF3E8FE8)

        when (condition) {
            WeatherCondition.CLEAR -> {
                drawSun(sunColor)
            }

            WeatherCondition.MAINLY_CLEAR,
            WeatherCondition.PARTLY_CLOUDY -> {
                drawCircle(
                    color = sunColor,
                    radius = size.width * 0.21f,
                    center = Offset(
                        size.width * 0.36f,
                        size.height * 0.34f
                    )
                )

                drawCloud(
                    cloudColor = cloudColor,
                    yOffset = size.height * 0.12f
                )
            }

            WeatherCondition.RAIN,
            WeatherCondition.RAIN_SHOWERS,
            WeatherCondition.DRIZZLE -> {
                drawCloud(
                    cloudColor = darkCloud,
                    yOffset = 0f
                )

                repeat(3) { index ->
                    val x =
                        size.width *
                                (0.30f + index * 0.20f)

                    drawLine(
                        color = rainColor,
                        start = Offset(
                            x,
                            size.height * 0.68f
                        ),
                        end = Offset(
                            x - size.width * 0.04f,
                            size.height * 0.88f
                        ),
                        strokeWidth = size.width * 0.045f
                    )
                }
            }

            WeatherCondition.SNOW,
            WeatherCondition.SNOW_SHOWERS -> {
                drawCloud(
                    cloudColor = darkCloud,
                    yOffset = 0f
                )

                repeat(3) { index ->
                    drawCircle(
                        color = Color(0xFF66AEF5),
                        radius = size.width * 0.04f,
                        center = Offset(
                            size.width *
                                    (0.30f + index * 0.20f),
                            size.height * 0.82f
                        )
                    )
                }
            }

            WeatherCondition.THUNDERSTORM -> {
                drawCloud(
                    cloudColor = Color(0xFF7188A2),
                    yOffset = 0f
                )

                val path = Path()

                path.moveTo(
                    size.width * 0.53f,
                    size.height * 0.62f
                )

                path.lineTo(
                    size.width * 0.40f,
                    size.height * 0.79f
                )

                path.lineTo(
                    size.width * 0.52f,
                    size.height * 0.79f
                )

                path.lineTo(
                    size.width * 0.43f,
                    size.height * 0.96f
                )

                drawPath(
                    path = path,
                    color = Color(0xFFFFD23F),
                    style = Stroke(
                        width = size.width * 0.06f
                    )
                )
            }

            else -> {
                drawCloud(
                    cloudColor = darkCloud,
                    yOffset = 0f
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSun(
    color: Color
) {
    val center = Offset(
        size.width / 2f,
        size.height / 2f
    )

    drawCircle(
        color = color,
        radius = size.width * 0.23f,
        center = center
    )

    repeat(8) { index ->
        val angle =
            Math.toRadians((index * 45).toDouble())

        val cos =
            kotlin.math.cos(angle).toFloat()

        val sin =
            kotlin.math.sin(angle).toFloat()

        drawLine(
            color = color,
            start = Offset(
                center.x + cos * size.width * 0.33f,
                center.y + sin * size.width * 0.33f
            ),
            end = Offset(
                center.x + cos * size.width * 0.44f,
                center.y + sin * size.width * 0.44f
            ),
            strokeWidth = size.width * 0.035f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCloud(
    cloudColor: Color,
    yOffset: Float
) {
    drawCircle(
        color = cloudColor,
        radius = size.width * 0.20f,
        center = Offset(
            size.width * 0.34f,
            size.height * 0.48f + yOffset
        )
    )

    drawCircle(
        color = cloudColor,
        radius = size.width * 0.27f,
        center = Offset(
            size.width * 0.55f,
            size.height * 0.42f + yOffset
        )
    )

    drawCircle(
        color = cloudColor,
        radius = size.width * 0.18f,
        center = Offset(
            size.width * 0.73f,
            size.height * 0.51f + yOffset
        )
    )

    drawRoundRect(
        color = cloudColor,
        topLeft = Offset(
            size.width * 0.18f,
            size.height * 0.48f + yOffset
        ),
        size = androidx.compose.ui.geometry.Size(
            size.width * 0.68f,
            size.height * 0.23f
        ),
        cornerRadius =
            androidx.compose.ui.geometry.CornerRadius(
                size.width * 0.12f,
                size.width * 0.12f
            )
    )
}

@Composable
private fun RainDropIcon(
    color: Color,
    modifier: Modifier = Modifier.size(16.dp)
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
    ) {
        val path = Path()

        path.moveTo(
            size.width * 0.50f,
            size.height * 0.06f
        )

        path.cubicTo(
            size.width * 0.22f,
            size.height * 0.38f,
            size.width * 0.20f,
            size.height * 0.70f,
            size.width * 0.50f,
            size.height * 0.91f
        )

        path.cubicTo(
            size.width * 0.80f,
            size.height * 0.70f,
            size.width * 0.78f,
            size.height * 0.38f,
            size.width * 0.50f,
            size.height * 0.06f
        )

        drawPath(
            path = path,
            color = color
        )
    }
}

@Composable
private fun DetailIcon(
    type: DetailIconType
) {
    val color =
        when (type) {
            DetailIconType.RAIN ->
                Color(0xFF398EE8)

            DetailIconType.WIND ->
                Color(0xFF527DAA)

            DetailIconType.TEMP ->
                Color(0xFFFF8A55)

            DetailIconType.HUMIDITY ->
                Color(0xFF3D9AE8)
        }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                color.copy(alpha = 0.12f)
            ),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            DetailIconType.RAIN,
            DetailIconType.HUMIDITY -> {
                RainDropIcon(
                    color = color,
                    modifier = Modifier.size(16.dp)
                )
            }

            DetailIconType.WIND -> {
                WindIcon(color)
            }

            DetailIconType.TEMP -> {
                TemperatureIcon(color)
            }
        }
    }
}

@Composable
private fun WindIcon(
    color: Color
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(18.dp)
    ) {
        repeat(3) { index ->
            val y =
                size.height *
                        (0.28f + index * 0.22f)

            drawLine(
                color = color,
                start = Offset(
                    size.width * 0.10f,
                    y
                ),
                end = Offset(
                    size.width *
                            (0.72f + index * 0.06f),
                    y
                ),
                strokeWidth = 1.8.dp.toPx()
            )
        }
    }
}

@Composable
private fun TemperatureIcon(
    color: Color
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(18.dp)
    ) {
        drawCircle(
            color = color,
            radius = size.width * 0.20f,
            center = Offset(
                size.width * 0.50f,
                size.height * 0.75f
            )
        )

        drawRoundRect(
            color = color,
            topLeft = Offset(
                size.width * 0.43f,
                size.height * 0.10f
            ),
            size =
                androidx.compose.ui.geometry.Size(
                    size.width * 0.14f,
                    size.height * 0.67f
                ),
            cornerRadius =
                androidx.compose.ui.geometry.CornerRadius(
                    20f,
                    20f
                )
        )
    }
}

@Composable
private fun CalendarIcon() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(
                Color.White.copy(alpha = 0.14f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "7",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LocationPin() {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(17.dp)
    ) {
        drawCircle(
            color = Color.White,
            radius = size.width * 0.29f,
            center = Offset(
                size.width / 2f,
                size.height * 0.40f
            ),
            style = Stroke(
                width = 1.7.dp.toPx()
            )
        )

        drawCircle(
            color = Color.White,
            radius = size.width * 0.08f,
            center = Offset(
                size.width / 2f,
                size.height * 0.40f
            )
        )

        drawLine(
            color = Color.White,
            start = Offset(
                size.width * 0.34f,
                size.height * 0.63f
            ),
            end = Offset(
                size.width * 0.50f,
                size.height * 0.88f
            ),
            strokeWidth = 1.7.dp.toPx()
        )

        drawLine(
            color = Color.White,
            start = Offset(
                size.width * 0.66f,
                size.height * 0.63f
            ),
            end = Offset(
                size.width * 0.50f,
                size.height * 0.88f
            ),
            strokeWidth = 1.7.dp.toPx()
        )
    }
}

@Composable
private fun SearchIcon(
    color: Color
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(20.dp)
    ) {
        drawCircle(
            color = color,
            radius = size.width * 0.28f,
            center = Offset(
                size.width * 0.40f,
                size.height * 0.40f
            ),
            style = Stroke(
                width = 1.8.dp.toPx()
            )
        )

        drawLine(
            color = color,
            start = Offset(
                size.width * 0.61f,
                size.height * 0.61f
            ),
            end = Offset(
                size.width * 0.85f,
                size.height * 0.85f
            ),
            strokeWidth = 1.8.dp.toPx()
        )
    }
}

private fun cityImageResource(
    cityName: String
): Int? {
    return when (
        cityName
            .trim()
            .lowercase()
    ) {
        "istanbul",
        "i̇stanbul" ->
            R.drawable.city_istanbul

        "ankara" ->
            R.drawable.city_ankara

        "izmir",
        "i̇zmir" ->
            R.drawable.city_izmir

        "bursa" ->
            R.drawable.city_bursa

        else -> null
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF246CC0),
                        Color(0xFF8CCAF4)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White
        )
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF246CC0))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White
        )
    }
}