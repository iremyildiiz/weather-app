package com.example.havadurumu.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.havadurumu.MapCityWeather
import com.example.havadurumu.MapWeatherViewModel
import com.example.havadurumu.model.City
import com.example.havadurumu.model.WeatherCondition
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.roundToInt

private enum class MapLayer {
    TEMPERATURE,
    RAIN,
    WIND
}

@Composable
fun WeatherMapScreen(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapWeatherViewModel = viewModel()
) {
    var selectedLayer by remember {
        mutableStateOf(MapLayer.TEMPERATURE)
    }

    var selectedCityWeather by remember {
        mutableStateOf<MapCityWeather?>(null)
    }

    val cityWeather by viewModel.cityWeather.collectAsState()

    val turkey = remember {
        LatLng(39.0, 35.0)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            turkey,
            5.4f
        )
    }

    val mapProperties = remember {
        MapProperties(
            isBuildingEnabled = false,
            isIndoorEnabled = false,
            isTrafficEnabled = false
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            compassEnabled = true,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapClick = {
                selectedCityWeather = null
            }
        ) {
            cityWeather.forEach { item ->
                val forecast = item.forecast

                if (
                    !item.isLoading &&
                    !item.error &&
                    forecast != null
                ) {
                    val markerTitle = when (selectedLayer) {
                        MapLayer.TEMPERATURE -> {
                            "${forecast.current.temperatureC.roundToInt()}°"
                        }

                        MapLayer.RAIN -> {
                            val precipitation =
                                forecast.hourly.firstOrNull {
                                    !it.time.isBefore(
                                        forecast.current.observedAt
                                    )
                                }?.precipitationProbability

                            "${precipitation ?: 0}%"
                        }

                        MapLayer.WIND -> {
                            val wind =
                                forecast.current.windSpeedKmh
                                    ?.roundToInt()

                            if (wind != null) {
                                "$wind km/sa"
                            } else {
                                "--"
                            }
                        }
                    }

                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                item.city.latitude,
                                item.city.longitude
                            )
                        ),
                        title = markerTitle,
                        snippet = item.city.name,
                        onClick = {
                            selectedCityWeather = item
                            true
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 18.dp
                )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        Color(0xFF061B31)
                            .copy(alpha = 0.94f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                ) {
                    Text(
                        text = "Hava Haritası",
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        ),
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(13.dp)
                    )

                    MapLayerSelector(
                        selectedLayer = selectedLayer,
                        onLayerSelected = {
                            selectedLayer = it
                        }
                    )
                }
            }
        }

        if (cityWeather.any { it.isLoading }) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 18.dp
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        Color(0xFF061B31)
                            .copy(alpha = 0.94f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 14.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = "Canlı hava verileri yükleniyor...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else if (selectedCityWeather != null) {
            SelectedCityCard(
                item = selectedCityWeather!!,
                selectedLayer = selectedLayer,
                onDetailClick = {
                    onCityClick(selectedCityWeather!!.city)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 18.dp
                    )
            )
        } else {
            MapStatusCard(
                selectedLayer = selectedLayer,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 18.dp
                    )
            )
        }
    }
}

@Composable
private fun SelectedCityCard(
    item: MapCityWeather,
    selectedLayer: MapLayer,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forecast = item.forecast ?: return

    val condition =
        WeatherCondition.fromWeatherCode(
            forecast.current.weatherCode
        )

    val currentPrecipitation =
        forecast.hourly.firstOrNull {
            !it.time.isBefore(
                forecast.current.observedAt
            )
        }?.precipitationProbability

    val today = forecast.daily.firstOrNull()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                Color(0xFF061B31)
                    .copy(alpha = 0.96f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.city.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = condition.label,
                        color = Color.White.copy(
                            alpha = 0.68f
                        ),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text =
                        "${forecast.current.temperatureC.roundToInt()}°",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                WeatherMiniValue(
                    title = "Hissedilen",
                    value =
                        forecast.current
                            .apparentTemperatureC
                            ?.let {
                                "${it.roundToInt()}°"
                            }
                            ?: "--"
                )

                WeatherMiniValue(
                    title = "Yağış",
                    value =
                        "${currentPrecipitation ?: 0}%"
                )

                WeatherMiniValue(
                    title = "Rüzgar",
                    value =
                        forecast.current
                            .windSpeedKmh
                            ?.let {
                                "${it.roundToInt()} km/sa"
                            }
                            ?: "--"
                )
            }

            if (today != null) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text =
                        "En yüksek ${today.maxTemperatureC.roundToInt()}°  •  " +
                                "En düşük ${today.minTemperatureC.roundToInt()}°",
                    color = Color.White.copy(
                        alpha = 0.72f
                    ),
                    fontSize = 11.sp
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedLayer) {
                        MapLayer.TEMPERATURE ->
                            "Canlı sıcaklık"

                        MapLayer.RAIN ->
                            "Güncel yağış olasılığı"

                        MapLayer.WIND ->
                            "Güncel rüzgar hızı"
                    },
                    color = Color.White.copy(
                        alpha = 0.55f
                    ),
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape =
                                RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            onDetailClick()
                        }
                        .padding(
                            horizontal = 18.dp,
                            vertical = 10.dp
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = "Detayı Gör",
                        color = Color(0xFF174C7A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherMiniValue(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = title,
            color = Color.White.copy(
                alpha = 0.55f
            ),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun MapStatusCard(
    selectedLayer: MapLayer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                Color(0xFF061B31)
                    .copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        ) {
            Text(
                text = when (selectedLayer) {
                    MapLayer.TEMPERATURE ->
                        "Sıcaklık"

                    MapLayer.RAIN ->
                        "Yağış"

                    MapLayer.WIND ->
                        "Rüzgar"
                },
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Detayları görmek için haritadaki bir şehre dokun",
                color = Color.White.copy(
                    alpha = 0.65f
                ),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun MapLayerSelector(
    selectedLayer: MapLayer,
    onLayerSelected: (MapLayer) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    Color(0xFF071F38)
                        .copy(alpha = 0.95f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(5.dp),
        horizontalArrangement =
            Arrangement.spacedBy(5.dp)
    ) {
        LayerButton(
            title = "Sıcaklık",
            selected =
                selectedLayer ==
                        MapLayer.TEMPERATURE,
            onClick = {
                onLayerSelected(
                    MapLayer.TEMPERATURE
                )
            },
            modifier = Modifier.weight(1f)
        )

        LayerButton(
            title = "Yağış",
            selected =
                selectedLayer ==
                        MapLayer.RAIN,
            onClick = {
                onLayerSelected(
                    MapLayer.RAIN
                )
            },
            modifier = Modifier.weight(1f)
        )

        LayerButton(
            title = "Rüzgar",
            selected =
                selectedLayer ==
                        MapLayer.WIND,
            onClick = {
                onLayerSelected(
                    MapLayer.WIND
                )
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LayerButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color =
                    if (selected) {
                        Color.White
                    } else {
                        Color.Transparent
                    },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                vertical = 11.dp,
                horizontal = 6.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color =
                if (selected) {
                    Color(0xFF174C7A)
                } else {
                    Color.White.copy(
                        alpha = 0.80f
                    )
                },
            fontSize = 12.sp,
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
            textAlign = TextAlign.Center
        )
    }
}