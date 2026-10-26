package com.example.havadurumu.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.havadurumu.model.WeatherCondition
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WeatherBackground(
    condition: WeatherCondition,
    modifier: Modifier = Modifier
) {

    val transition = rememberInfiniteTransition(
        label = "weatherAtmosphere"
    )

    val slowMovement by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(26000),
            repeatMode = RepeatMode.Restart
        ),
        label = "slowMovement"
    )

    val fastMovement by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Restart
        ),
        label = "fastMovement"
    )

    val glowPulse by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val colors = backgroundColors(condition)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors)
            )
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            when (condition) {

                WeatherCondition.CLEAR -> {
                    drawSunAtmosphere(
                        pulse = glowPulse
                    )
                }

                WeatherCondition.MAINLY_CLEAR -> {
                    drawSunAtmosphere(
                        pulse = glowPulse
                    )

                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 3,
                        opacity = 0.10f
                    )
                }

                WeatherCondition.PARTLY_CLOUDY -> {
                    drawSunAtmosphere(
                        pulse = glowPulse
                    )

                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 5,
                        opacity = 0.14f
                    )
                }

                WeatherCondition.OVERCAST -> {
                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 7,
                        opacity = 0.18f
                    )
                }

                WeatherCondition.DRIZZLE -> {
                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 6,
                        opacity = 0.16f
                    )

                    drawRain(
                        progress = fastMovement,
                        count = 45,
                        opacity = 0.24f
                    )
                }

                WeatherCondition.RAIN -> {
                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 7,
                        opacity = 0.19f
                    )

                    drawRain(
                        progress = fastMovement,
                        count = 85,
                        opacity = 0.32f
                    )
                }

                WeatherCondition.RAIN_SHOWERS -> {
                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 8,
                        opacity = 0.20f
                    )

                    drawRain(
                        progress = fastMovement,
                        count = 110,
                        opacity = 0.36f
                    )
                }

                WeatherCondition.SNOW,
                WeatherCondition.SNOW_SHOWERS -> {
                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 5,
                        opacity = 0.15f
                    )

                    drawSnow(
                        progress = fastMovement,
                        count = 65
                    )
                }

                WeatherCondition.FOG -> {
                    drawFog(
                        progress = slowMovement
                    )
                }

                WeatherCondition.THUNDERSTORM -> {
                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 9,
                        opacity = 0.22f
                    )

                    drawRain(
                        progress = fastMovement,
                        count = 120,
                        opacity = 0.38f
                    )
                }

                WeatherCondition.UNKNOWN -> {
                    drawAtmosphericClouds(
                        progress = slowMovement,
                        density = 4,
                        opacity = 0.12f
                    )
                }
            }
        }
    }
}

private fun backgroundColors(
    condition: WeatherCondition
): List<Color> {

    return when (condition) {

        WeatherCondition.CLEAR ->
            listOf(
                Color(0xFF2774D8),
                Color(0xFF5599E5),
                Color(0xFF91C3F3)
            )

        WeatherCondition.MAINLY_CLEAR ->
            listOf(
                Color(0xFF397FD3),
                Color(0xFF68A3E4),
                Color(0xFFA8CDF0)
            )

        WeatherCondition.PARTLY_CLOUDY ->
            listOf(
                Color(0xFF4A7EBA),
                Color(0xFF7EA6CF),
                Color(0xFFB7CEE4)
            )

        WeatherCondition.OVERCAST ->
            listOf(
                Color(0xFF52687E),
                Color(0xFF7F94A7),
                Color(0xFFAEBCC8)
            )

        WeatherCondition.DRIZZLE,
        WeatherCondition.RAIN,
        WeatherCondition.RAIN_SHOWERS ->
            listOf(
                Color(0xFF273B50),
                Color(0xFF49637B),
                Color(0xFF758EA4)
            )

        WeatherCondition.SNOW,
        WeatherCondition.SNOW_SHOWERS ->
            listOf(
                Color(0xFF6D8BA7),
                Color(0xFFA2B8CB),
                Color(0xFFD6E1E9)
            )

        WeatherCondition.FOG ->
            listOf(
                Color(0xFF758591),
                Color(0xFFA1ADB5),
                Color(0xFFCBD2D7)
            )

        WeatherCondition.THUNDERSTORM ->
            listOf(
                Color(0xFF172231),
                Color(0xFF34495E),
                Color(0xFF5D7184)
            )

        WeatherCondition.UNKNOWN ->
            listOf(
                Color(0xFF3978D4),
                Color(0xFF72A6DF),
                Color(0xFFB4D2ED)
            )
    }
}

private fun DrawScope.drawSunAtmosphere(
    pulse: Float
) {

    val center = Offset(
        x = size.width * 0.82f,
        y = size.height * 0.16f
    )

    val sunRadius =
        size.width * 0.105f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFE7A1)
                    .copy(alpha = 0.30f),
                Color(0xFFFFE7A1)
                    .copy(alpha = 0.12f),
                Color.Transparent
            ),
            center = center,
            radius = sunRadius * 4.8f * pulse
        ),
        center = center,
        radius = sunRadius * 4.8f * pulse
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF5CF),
                Color(0xFFFFD978)
            ),
            center = center,
            radius = sunRadius
        ),
        center = center,
        radius = sunRadius
    )

    drawCircle(
        color = Color.White.copy(
            alpha = 0.08f
        ),
        center = center,
        radius = sunRadius * 1.7f * pulse
    )
}

private fun DrawScope.drawAtmosphericClouds(
    progress: Float,
    density: Int,
    opacity: Float
) {

    repeat(density) { index ->

        val layer =
            index % 3

        val width =
            size.width * (
                    0.65f +
                            layer * 0.16f
                    )

        val height =
            width * 0.28f

        val travelDistance =
            size.width + width * 1.5f

        val startOffset =
            index.toFloat() /
                    density.toFloat()

        val localProgress =
            (progress + startOffset) % 1f

        val x =
            -width +
                    localProgress *
                    travelDistance

        val y =
            size.height *
                    (
                            0.08f +
                                    (index % 5) * 0.105f
                            )

        val alpha =
            opacity *
                    (
                            0.70f +
                                    layer * 0.12f
                            )

        drawSoftCloudLayer(
            x = x,
            y = y,
            width = width,
            height = height,
            alpha = alpha
        )
    }
}

private fun DrawScope.drawSoftCloudLayer(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    alpha: Float
) {

    val cloudColor =
        Color.White.copy(alpha = alpha)

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                Color.White.copy(
                    alpha = alpha * 0.55f
                ),
                Color.Transparent
            ),
            center = Offset(
                x + width * 0.50f,
                y + height * 0.45f
            ),
            radius = width * 0.58f
        ),
        topLeft = Offset(
            x = x,
            y = y
        ),
        size = Size(
            width = width,
            height = height
        )
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                Color.Transparent
            ),
            center = Offset(
                x + width * 0.32f,
                y
            ),
            radius = width * 0.30f
        ),
        topLeft = Offset(
            x = x + width * 0.08f,
            y = y - height * 0.45f
        ),
        size = Size(
            width = width * 0.55f,
            height = height * 1.25f
        )
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                Color.Transparent
            ),
            center = Offset(
                x + width * 0.65f,
                y
            ),
            radius = width * 0.28f
        ),
        topLeft = Offset(
            x = x + width * 0.42f,
            y = y - height * 0.30f
        ),
        size = Size(
            width = width * 0.50f,
            height = height * 1.10f
        )
    )
}

private fun DrawScope.drawRain(
    progress: Float,
    count: Int,
    opacity: Float
) {

    val random = Random(2026)

    repeat(count) {

        val x =
            random.nextFloat() *
                    (size.width + 100f)

        val initialY =
            random.nextFloat() *
                    size.height

        val speed =
            0.7f +
                    random.nextFloat() * 1.5f

        val y =
            (
                    initialY +
                            progress *
                            size.height *
                            speed
                    ) % size.height

        val length =
            18f +
                    random.nextFloat() * 22f

        drawLine(
            color = Color.White.copy(
                alpha = opacity
            ),
            start = Offset(
                x = x,
                y = y
            ),
            end = Offset(
                x = x - 5f,
                y = y + length
            ),
            strokeWidth =
                1.4f +
                        random.nextFloat()
        )
    }
}

private fun DrawScope.drawSnow(
    progress: Float,
    count: Int
) {

    val random = Random(1999)

    repeat(count) { index ->

        val startX =
            random.nextFloat() *
                    size.width

        val startY =
            random.nextFloat() *
                    size.height

        val speed =
            0.18f +
                    random.nextFloat() * 0.45f

        val y =
            (
                    startY +
                            progress *
                            size.height *
                            speed
                    ) % size.height

        val sway =
            sin(
                progress *
                        6.283f +
                        index * 0.7f
            ) * 14f

        val radius =
            1.8f +
                    random.nextFloat() * 4.2f

        drawCircle(
            color = Color.White.copy(
                alpha =
                    0.45f +
                            random.nextFloat() * 0.40f
            ),
            radius = radius,
            center = Offset(
                x = startX + sway,
                y = y
            )
        )
    }
}

private fun DrawScope.drawFog(
    progress: Float
) {

    repeat(6) { index ->

        val fogWidth =
            size.width * 1.25f

        val startOffset =
            index / 6f

        val localProgress =
            (progress + startOffset) % 1f

        val x =
            -fogWidth +
                    localProgress *
                    (size.width + fogWidth)

        val y =
            size.height *
                    (
                            0.12f +
                                    index * 0.13f
                            )

        drawOval(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(
                        alpha = 0.10f
                    ),
                    Color.White.copy(
                        alpha = 0.22f
                    ),
                    Color.White.copy(
                        alpha = 0.10f
                    ),
                    Color.Transparent
                )
            ),
            topLeft = Offset(
                x = x,
                y = y
            ),
            size = Size(
                width = fogWidth,
                height = size.height * 0.10f
            )
        )
    }
}