package com.example.havadurumu.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import com.example.havadurumu.FavoriteWeather
import com.example.havadurumu.FavoritesViewModel
import com.example.havadurumu.R
import com.example.havadurumu.model.City
import com.example.havadurumu.model.WeatherCondition

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier
) {

    val favorites by viewModel.favorites.collectAsState()
    val favoriteWeather by viewModel.favoriteWeather.collectAsState()

    LaunchedEffect(favorites) {
        if (favorites.isNotEmpty()) {
            viewModel.loadFavoriteWeather()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FD))
    ) {

        FavoritesHero(
            favoriteCount = favorites.size
        )

        if (favorites.isEmpty()) {

            EmptyFavorites(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {

                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 24.dp,
                            bottom = 2.dp
                        )
                    ) {

                        Text(
                            text = "Favori Şehirlerin",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF101828)
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Anlık hava durumlarını buradan takip edebilirsin",
                            fontSize = 13.sp,
                            color = Color(0xFF71809A)
                        )
                    }
                }

                items(
                    items = favorites,
                    key = { city ->
                        city.id
                    }
                ) { city ->

                    val weather =
                        favoriteWeather.firstOrNull {
                            it.city.id == city.id
                        }

                    FavoriteWeatherCard(
                        city = city,
                        weather = weather,
                        onClick = {
                            onCityClick(city)
                        },
                        onDelete = {
                            viewModel.removeFavorite(city)
                        }
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesHero(
    favoriteCount: Int
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.city_istanbul
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF006BE6)
                                .copy(alpha = 0.76f),

                            Color(0xFF197FE5)
                                .copy(alpha = 0.58f),

                            Color(0xFF75B8F2)
                                .copy(alpha = 0.30f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 32.dp
                )
        ) {

            Text(
                text = "Favoriler",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "Takip ettiğin şehirler",
                color = Color.White.copy(
                    alpha = 0.90f
                ),
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(50.dp)
                    )
                    .background(
                        Color.White.copy(
                            alpha = 0.18f
                        )
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    )
            ) {

                Text(
                    text =
                        if (favoriteCount == 0) {
                            "Henüz favori şehir yok"
                        } else {
                            "$favoriteCount şehir takip ediliyor"
                        },
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyFavorites(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "♡",
                    fontSize = 52.sp,
                    color = Color(0xFF1677F2)
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Henüz favori şehir yok",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF101828)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Takip etmek istediğin şehirleri\nfavorilerine ekleyebilirsin.",
                fontSize = 14.sp,
                color = Color(0xFF71809A)
            )
        }
    }
}

@Composable
private fun FavoriteWeatherCard(
    city: City,
    weather: FavoriteWeather?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val forecast = weather?.forecast

    val condition =
        forecast?.current?.weatherCode?.let {
            WeatherCondition.fromWeatherCode(it)
        }

    val today =
        forecast?.daily?.firstOrNull()

    Card(
        modifier = Modifier
            .padding(
                horizontal = 20.dp
            )
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 15.dp,
                    end = 8.dp,
                    top = 14.dp,
                    bottom = 14.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                FavoriteCityThumbnail(
                    city = city
                )

                Spacer(
                    modifier = Modifier.width(13.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = city.name,
                        fontSize = 18.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color = Color(0xFF101828)
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = city.country
                            ?: "Türkiye",
                        fontSize = 12.sp,
                        color = Color(0xFF71809A)
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Text(
                        text = "♥",
                        fontSize = 24.sp,
                        color = Color(0xFFE85D75)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            when {

                weather == null ||
                        weather.isLoading -> {

                    FavoriteLoadingContent()
                }

                weather.error ||
                        forecast == null -> {

                    FavoriteErrorContent()
                }

                else -> {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        FavoriteWeatherIcon(
                            condition =
                                condition
                                    ?: WeatherCondition.CLEAR,
                            modifier =
                                Modifier.size(40.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(11.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    condition?.label
                                        ?: "Hava durumu",
                                fontSize = 13.sp,
                                fontWeight =
                                    FontWeight.SemiBold,
                                color =
                                    Color(0xFF40546D)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(5.dp)
                            )

                            Row {

                                Text(
                                    text =
                                        "Y: ${
                                            today
                                                ?.maxTemperatureC
                                                ?.toInt()
                                                ?: "--"
                                        }°",
                                    fontSize = 11.sp,
                                    color =
                                        Color(0xFF71809A)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(10.dp)
                                )

                                Text(
                                    text =
                                        "D: ${
                                            today
                                                ?.minTemperatureC
                                                ?.toInt()
                                                ?: "--"
                                        }°",
                                    fontSize = 11.sp,
                                    color =
                                        Color(0xFF71809A)
                                )
                            }
                        }

                        Text(
                            text =
                                "${forecast.current.temperatureC.toInt()}°",
                            fontSize = 35.sp,
                            fontWeight =
                                FontWeight.Light,
                            color = Color(0xFF101828)
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCityThumbnail(
    city: City
) {

    val image =
        favoriteCityImageResource(
            city.name
        )

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(
                Color(0xFFDCEEFF)
            )
    ) {

        if (image != null) {

            Image(
                painter =
                    painterResource(
                        id = image
                    ),
                contentDescription =
                    city.name,
                modifier =
                    Modifier.fillMaxSize(),
                contentScale =
                    ContentScale.Crop
            )

        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF9ED4FF),
                                Color(0xFFDDEFFF)
                            )
                        )
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "☁",
                    fontSize = 25.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun FavoriteLoadingContent() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            strokeWidth = 2.dp,
            color = Color(0xFF1677F2)
        )

        Spacer(
            modifier = Modifier.width(11.dp)
        )

        Text(
            text = "Hava durumu yükleniyor...",
            fontSize = 12.sp,
            color = Color(0xFF71809A)
        )
    }
}

@Composable
private fun FavoriteErrorContent() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        contentAlignment =
            Alignment.CenterStart
    ) {

        Text(
            text = "Hava durumu alınamadı",
            fontSize = 12.sp,
            color = Color(0xFF9A6670)
        )
    }
}

@Composable
private fun FavoriteWeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
    ) {

        val sun =
            Color(0xFFFFC83D)

        val cloud =
            Color(0xFF8EA9C4)

        val lightCloud =
            Color(0xFFDDEAF5)

        val rain =
            Color(0xFF398FE8)

        when (condition) {

            WeatherCondition.CLEAR -> {

                drawFavoriteSun(sun)
            }

            WeatherCondition.MAINLY_CLEAR,
            WeatherCondition.PARTLY_CLOUDY -> {

                drawCircle(
                    color = sun,
                    radius =
                        size.width * 0.20f,
                    center = Offset(
                        size.width * 0.34f,
                        size.height * 0.32f
                    )
                )

                drawFavoriteCloud(
                    color = lightCloud,
                    yOffset =
                        size.height * 0.08f
                )
            }

            WeatherCondition.RAIN,
            WeatherCondition.RAIN_SHOWERS,
            WeatherCondition.DRIZZLE -> {

                drawFavoriteCloud(
                    cloud
                )

                repeat(3) { index ->

                    val x =
                        size.width *
                                (0.30f +
                                        index * 0.20f)

                    drawLine(
                        color = rain,
                        start = Offset(
                            x,
                            size.height * 0.68f
                        ),
                        end = Offset(
                            x -
                                    size.width * 0.04f,
                            size.height * 0.88f
                        ),
                        strokeWidth =
                            size.width * 0.045f
                    )
                }
            }

            WeatherCondition.SNOW,
            WeatherCondition.SNOW_SHOWERS -> {

                drawFavoriteCloud(
                    cloud
                )

                repeat(3) { index ->

                    drawCircle(
                        color =
                            Color(0xFF68AFF3),
                        radius =
                            size.width * 0.04f,
                        center = Offset(
                            size.width *
                                    (0.30f +
                                            index * 0.20f),
                            size.height * 0.82f
                        )
                    )
                }
            }

            WeatherCondition.THUNDERSTORM -> {

                drawFavoriteCloud(
                    Color(0xFF738AA2)
                )

                val bolt = Path()

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
                            size.width * 0.06f
                    )
                )
            }

            else -> {

                drawFavoriteCloud(
                    cloud
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFavoriteSun(
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
                (index * 45).toDouble()
            )

        val cos =
            kotlin.math.cos(angle)
                .toFloat()

        val sin =
            kotlin.math.sin(angle)
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
                size.width * 0.035f
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFavoriteCloud(
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

private fun favoriteCityImageResource(
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

        else ->
            null
    }
}