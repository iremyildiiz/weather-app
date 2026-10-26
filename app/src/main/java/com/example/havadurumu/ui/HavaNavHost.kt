package com.example.havadurumu.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.havadurumu.CityViewModel
import com.example.havadurumu.FavoritesViewModel
import com.example.havadurumu.ForecastViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HavaNavHost() {

    val navController = rememberNavController()

    val cityViewModel: CityViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry?.destination?.route

    val showBottomBar =
        currentRoute == "cities" ||
                currentRoute == "map" ||
                currentRoute == "favorites"

    Scaffold(
        contentWindowInsets = WindowInsets(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0
        ),

        bottomBar = {

            if (showBottomBar) {

                WeatherBottomBar(
                    currentRoute = currentRoute,

                    onCitiesClick = {

                        if (currentRoute != "cities") {

                            navController.navigate("cities") {

                                popUpTo("cities") {
                                    inclusive = false
                                }

                                launchSingleTop = true
                            }
                        }
                    },

                    onMapClick = {

                        if (currentRoute != "map") {

                            navController.navigate("map") {
                                launchSingleTop = true
                            }
                        }
                    },

                    onFavoritesClick = {

                        if (currentRoute != "favorites") {

                            navController.navigate("favorites") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,

            startDestination = "cities",

            modifier = Modifier.padding(
                bottom =
                    innerPadding.calculateBottomPadding()
            )
        ) {

            // =========================================
            // ŞEHİRLER
            // =========================================

            composable("cities") {

                CityScreen(
                    viewModel = cityViewModel,

                    onCityClick = { city ->

                        cityViewModel.selectCity(city)

                        val encodedName =
                            URLEncoder.encode(
                                city.name,
                                StandardCharsets.UTF_8.toString()
                            )

                        navController.navigate(
                            "forecast/" +
                                    "${city.latitude}/" +
                                    "${city.longitude}/" +
                                    encodedName
                        )
                    }
                )
            }

            // =========================================
            // HARİTA
            // =========================================

            composable("map") {

                WeatherMapScreen(
                    onCityClick = { city ->

                        cityViewModel.selectCity(city)

                        val encodedName =
                            URLEncoder.encode(
                                city.name,
                                StandardCharsets.UTF_8.toString()
                            )

                        navController.navigate(
                            "forecast/" +
                                    "${city.latitude}/" +
                                    "${city.longitude}/" +
                                    encodedName
                        )
                    }
                )
            }

            // =========================================
            // FAVORİLER
            // =========================================

            composable("favorites") {

                FavoritesScreen(
                    viewModel =
                        favoritesViewModel,

                    onCityClick = { city ->

                        cityViewModel.selectCity(city)

                        val encodedName =
                            URLEncoder.encode(
                                city.name,
                                StandardCharsets.UTF_8.toString()
                            )

                        navController.navigate(
                            "forecast/" +
                                    "${city.latitude}/" +
                                    "${city.longitude}/" +
                                    encodedName
                        )
                    }
                )
            }

            // =========================================
            // HAVA DETAYI
            // =========================================

            composable(
                route =
                    "forecast/{latitude}/{longitude}/{cityName}",

                arguments = listOf(

                    navArgument("latitude") {
                        type = NavType.StringType
                    },

                    navArgument("longitude") {
                        type = NavType.StringType
                    },

                    navArgument("cityName") {
                        type = NavType.StringType
                    }
                )
            ) { forecastEntry ->

                val latitude =
                    forecastEntry.arguments
                        ?.getString("latitude")
                        ?.toDoubleOrNull()
                        ?: return@composable

                val longitude =
                    forecastEntry.arguments
                        ?.getString("longitude")
                        ?.toDoubleOrNull()
                        ?: return@composable

                val cityName =
                    URLDecoder.decode(
                        forecastEntry.arguments
                            ?.getString("cityName")
                            .orEmpty(),

                        StandardCharsets.UTF_8.toString()
                    )

                val forecastViewModel:
                        ForecastViewModel = viewModel()

                ForecastScreen(
                    latitude = latitude,
                    longitude = longitude,
                    cityName = cityName,

                    viewModel =
                        forecastViewModel,

                    favoritesViewModel =
                        favoritesViewModel,

                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

// =====================================================
// BOTTOM BAR
// =====================================================

@Composable
private fun WeatherBottomBar(
    currentRoute: String?,
    onCitiesClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {

    NavigationBar(
        containerColor =
            Color(0xFF092E55),

        tonalElevation =
            0.dp
    ) {

        // =============================================
        // ŞEHİRLER
        // =============================================

        NavigationBarItem(
            selected =
                currentRoute == "cities",

            onClick =
                onCitiesClick,

            icon = {

                CitiesIcon(
                    selected =
                        currentRoute == "cities"
                )
            },

            label = {

                Text(
                    text = "Şehirler"
                )
            },

            colors =
                bottomBarColors()
        )

        // =============================================
        // HARİTA
        // =============================================

        NavigationBarItem(
            selected =
                currentRoute == "map",

            onClick =
                onMapClick,

            icon = {

                MapIcon(
                    selected =
                        currentRoute == "map"
                )
            },

            label = {

                Text(
                    text = "Harita"
                )
            },

            colors =
                bottomBarColors()
        )

        // =============================================
        // FAVORİLER
        // =============================================

        NavigationBarItem(
            selected =
                currentRoute == "favorites",

            onClick =
                onFavoritesClick,

            icon = {

                FavoriteIcon(
                    selected =
                        currentRoute == "favorites"
                )
            },

            label = {

                Text(
                    text = "Favoriler"
                )
            },

            colors =
                bottomBarColors()
        )
    }
}

// =====================================================
// COLORS
// =====================================================

@Composable
private fun bottomBarColors() =
    NavigationBarItemDefaults.colors(

        selectedIconColor =
            Color.White,

        selectedTextColor =
            Color.White,

        indicatorColor =
            Color(0xFF1685F8),

        unselectedIconColor =
            Color.White.copy(
                alpha = 0.58f
            ),

        unselectedTextColor =
            Color.White.copy(
                alpha = 0.58f
            )
    )

// =====================================================
// CITIES ICON
// =====================================================

@Composable
private fun CitiesIcon(
    selected: Boolean
) {

    val color =
        if (selected) {
            Color.White
        } else {
            Color.White.copy(
                alpha = 0.58f
            )
        }

    Canvas(
        modifier =
            Modifier.size(23.dp)
    ) {

        val stroke =
            1.8.dp.toPx()

        // SOL BİNA

        drawRect(
            color = color,

            topLeft =
                Offset(
                    size.width * 0.13f,
                    size.height * 0.38f
                ),

            size =
                androidx.compose.ui.geometry.Size(
                    size.width * 0.25f,
                    size.height * 0.47f
                ),

            style =
                Stroke(
                    width = stroke
                )
        )

        // ORTA BİNA

        drawRect(
            color = color,

            topLeft =
                Offset(
                    size.width * 0.39f,
                    size.height * 0.16f
                ),

            size =
                androidx.compose.ui.geometry.Size(
                    size.width * 0.25f,
                    size.height * 0.69f
                ),

            style =
                Stroke(
                    width = stroke
                )
        )

        // SAĞ BİNA

        drawRect(
            color = color,

            topLeft =
                Offset(
                    size.width * 0.65f,
                    size.height * 0.48f
                ),

            size =
                androidx.compose.ui.geometry.Size(
                    size.width * 0.22f,
                    size.height * 0.37f
                ),

            style =
                Stroke(
                    width = stroke
                )
        )

        // PENCERELER

        drawCircle(
            color = color,
            radius = 1.dp.toPx(),
            center = Offset(
                size.width * 0.51f,
                size.height * 0.33f
            )
        )

        drawCircle(
            color = color,
            radius = 1.dp.toPx(),
            center = Offset(
                size.width * 0.51f,
                size.height * 0.51f
            )
        )

        drawCircle(
            color = color,
            radius = 1.dp.toPx(),
            center = Offset(
                size.width * 0.51f,
                size.height * 0.68f
            )
        )
    }
}

// =====================================================
// MAP ICON
// =====================================================

@Composable
private fun MapIcon(
    selected: Boolean
) {

    val color =
        if (selected) {
            Color.White
        } else {
            Color.White.copy(
                alpha = 0.58f
            )
        }

    Canvas(
        modifier =
            Modifier.size(23.dp)
    ) {

        val path =
            Path()

        path.moveTo(
            size.width * 0.08f,
            size.height * 0.24f
        )

        path.lineTo(
            size.width * 0.34f,
            size.height * 0.12f
        )

        path.lineTo(
            size.width * 0.65f,
            size.height * 0.25f
        )

        path.lineTo(
            size.width * 0.92f,
            size.height * 0.13f
        )

        path.lineTo(
            size.width * 0.92f,
            size.height * 0.76f
        )

        path.lineTo(
            size.width * 0.65f,
            size.height * 0.88f
        )

        path.lineTo(
            size.width * 0.34f,
            size.height * 0.75f
        )

        path.lineTo(
            size.width * 0.08f,
            size.height * 0.87f
        )

        path.close()

        drawPath(
            path = path,

            color = color,

            style =
                Stroke(
                    width =
                        1.8.dp.toPx()
                )
        )

        drawLine(
            color = color,

            start =
                Offset(
                    size.width * 0.34f,
                    size.height * 0.13f
                ),

            end =
                Offset(
                    size.width * 0.34f,
                    size.height * 0.75f
                ),

            strokeWidth =
                1.8.dp.toPx()
        )

        drawLine(
            color = color,

            start =
                Offset(
                    size.width * 0.65f,
                    size.height * 0.25f
                ),

            end =
                Offset(
                    size.width * 0.65f,
                    size.height * 0.87f
                ),

            strokeWidth =
                1.8.dp.toPx()
        )
    }
}

// =====================================================
// FAVORITE ICON
// =====================================================

@Composable
private fun FavoriteIcon(
    selected: Boolean
) {

    val color =
        if (selected) {
            Color.White
        } else {
            Color.White.copy(
                alpha = 0.58f
            )
        }

    Canvas(
        modifier =
            Modifier.size(23.dp)
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

        if (selected) {

            drawPath(
                path = path,
                color = color
            )

        } else {

            drawPath(
                path = path,

                color = color,

                style =
                    Stroke(
                        width =
                            1.8.dp.toPx()
                    )
            )
        }
    }
}