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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havadurumu.CityViewModel
import com.example.havadurumu.R
import com.example.havadurumu.model.City

@Composable
fun CityScreen(
    viewModel: CityViewModel,
    onCityClick: (City) -> Unit = {},
    modifier: Modifier = Modifier
) {

    val query by viewModel.query.collectAsState()
    val cities by viewModel.cities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FD))
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = 40.dp
            )
        ) {

            // =========================================
            // ÜST ALAN
            // =========================================

            item {

                CityHero(
                    query = query,
                    onQueryChange = viewModel::onQueryChange
                )
            }

            // =========================================
            // ARAMA YAPILMIYORSA
            // =========================================

            if (query.isBlank()) {

                item {

                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 24.dp
                        )
                    ) {

                        // =============================
                        // SEÇİLİ ŞEHİR
                        // =============================

                        SectionTitle(
                            title = "Şu An Seçili"
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        SelectedCityCard(
                            city = selectedCity,
                            onClick = {
                                onCityClick(selectedCity)
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(28.dp)
                        )

                        // =============================
                        // POPÜLER
                        // =============================

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Popüler Şehirler",
                                modifier = Modifier.weight(1f),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF101828)
                            )

                            Text(
                                text = "Tümünü Gör",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1677F2)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        PopularCitiesGrid(
                            onCityClick = onCityClick
                        )

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )

                        // =============================
                        // TÜRKİYE
                        // =============================

                        SectionTitle(
                            title = "Türkiye Şehirleri"
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )
                    }
                }

                val groupedCities =
                    turkeyCities.groupBy {
                        it.name
                            .first()
                            .uppercaseChar()
                    }

                groupedCities.forEach { (letter, cityList) ->

                    item {

                        Text(
                            text = letter.toString(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1677F2),
                            modifier = Modifier.padding(
                                start = 22.dp,
                                top = 18.dp,
                                bottom = 7.dp
                            )
                        )
                    }

                    items(
                        items = cityList,
                        key = { city ->
                            city.id
                        }
                    ) { city ->

                        CityListCard(
                            city = city,
                            onClick = {
                                onCityClick(city)
                            }
                        )
                    }
                }
            }

            // =========================================
            // ARAMA YAPILIYORSA
            // =========================================

            else {

                item {

                    Text(
                        text = "Arama Sonuçları",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF101828),
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 28.dp,
                            bottom = 14.dp
                        )
                    )
                }

                when {

                    isLoading -> {

                        item {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                CircularProgressIndicator(
                                    color = Color(0xFF1677F2)
                                )
                            }
                        }
                    }

                    errorMessage != null -> {

                        item {

                            SearchMessageCard(
                                title = "Bir sorun oluştu",
                                message = errorMessage
                                    ?: "Şehirler yüklenemedi."
                            )
                        }
                    }

                    cities.isEmpty() -> {

                        item {

                            SearchMessageCard(
                                title = "Şehir bulunamadı",
                                message =
                                    "\"$query\" için başka bir arama deneyebilirsin."
                            )
                        }
                    }

                    else -> {

                        items(
                            items = cities,
                            key = { city ->
                                city.id
                            }
                        ) { city ->

                            SearchResultCard(
                                city = city,
                                onClick = {
                                    onCityClick(city)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// =====================================================
// HERO
// =====================================================

@Composable
private fun CityHero(
    query: String,
    onQueryChange: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
    ) {

        // İstanbul görselini hero arka planında kullanıyoruz

        Image(
            painter = painterResource(
                id = R.drawable.city_istanbul
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Fotoğrafın üstüne mavi katman
        // Yazılar daha rahat okunuyor

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF006BE6)
                                .copy(alpha = 0.72f),

                            Color(0xFF197FE5)
                                .copy(alpha = 0.48f),

                            Color(0xFF75B8F2)
                                .copy(alpha = 0.18f)
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
                text = "Şehirler",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "Hava durumunu keşfet",
                color = Color.White.copy(
                    alpha = 0.90f
                ),
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,

                placeholder = {

                    Text(
                        text = "Şehir veya ülke ara...",
                        color = Color(0xFF8190A8)
                    )
                },

                leadingIcon = {

                    SearchIcon(
                        color = Color(0xFF637895)
                    )
                },

                trailingIcon = {

                    if (query.isNotEmpty()) {

                        Text(
                            text = "×",
                            fontSize = 26.sp,
                            color = Color(0xFF637895),
                            modifier = Modifier
                                .clickable {
                                    onQueryChange("")
                                }
                                .padding(8.dp)
                        )
                    }
                },

                shape = RoundedCornerShape(24.dp),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White,

                        focusedBorderColor =
                            Color.Transparent,

                        unfocusedBorderColor =
                            Color.Transparent
                    )
            )
        }
    }
}

// =====================================================
// SEÇİLİ ŞEHİR
// =====================================================

@Composable
private fun SelectedCityCard(
    city: City,
    onClick: () -> Unit
) {

    val image =
        cityImageResource(city.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(25.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Transparent
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            if (image != null) {

                Image(
                    painter = painterResource(
                        id = image
                    ),
                    contentDescription = city.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF72B7F4),
                                    Color(0xFFBFE1FF)
                                )
                            )
                        )
                )
            }

            // Sol tarafı karartıyoruz.
            // Yazılar fotoğrafta kaybolmasın.

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF061A34)
                                    .copy(alpha = 0.72f),

                                Color(0xFF061A34)
                                    .copy(alpha = 0.25f),

                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = city.name,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text = city.country
                            ?: "Türkiye",
                        fontSize = 14.sp,
                        color = Color.White.copy(
                            alpha = 0.82f
                        )
                    )

                    Spacer(
                        modifier =
                            Modifier.height(11.dp)
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                Color.White.copy(
                                    alpha = 0.17f
                                ),
                                RoundedCornerShape(
                                    50.dp
                                )
                            )
                            .padding(
                                horizontal = 11.dp,
                                vertical = 6.dp
                            )
                    ) {

                        Text(
                            text = "Seçili şehir",
                            fontSize = 11.sp,
                            fontWeight =
                                FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                CircleArrow()
            }
        }
    }
}

// =====================================================
// POPÜLER ŞEHİRLER
// =====================================================

@Composable
private fun PopularCitiesGrid(
    onCityClick: (City) -> Unit
) {

    Column(
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            PopularCityCard(
                city = popularCities[0],
                temperature = "18°",
                modifier =
                    Modifier.weight(1f),
                onClick = {
                    onCityClick(
                        popularCities[0]
                    )
                }
            )

            PopularCityCard(
                city = popularCities[1],
                temperature = "16°",
                modifier =
                    Modifier.weight(1f),
                onClick = {
                    onCityClick(
                        popularCities[1]
                    )
                }
            )
        }

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            PopularCityCard(
                city = popularCities[2],
                temperature = "20°",
                modifier =
                    Modifier.weight(1f),
                onClick = {
                    onCityClick(
                        popularCities[2]
                    )
                }
            )

            PopularCityCard(
                city = popularCities[3],
                temperature = "15°",
                modifier =
                    Modifier.weight(1f),
                onClick = {
                    onCityClick(
                        popularCities[3]
                    )
                }
            )
        }
    }
}

@Composable
private fun PopularCityCard(
    city: City,
    temperature: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val image =
        cityImageResource(city.name)

    Card(
        modifier = modifier
            .height(178.dp)
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(23.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Transparent
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Box(
            modifier =
                Modifier.fillMaxSize()
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
            }

            // Gradient sayesinde alt yazılar okunuyor

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(
                                    alpha = 0.05f
                                ),

                                Color.Transparent,

                                Color(0xFF07172D)
                                    .copy(
                                        alpha = 0.78f
                                    )
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Text(
                    text = city.name,
                    fontSize = 18.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier =
                        Modifier.height(1.dp)
                )

                Text(
                    text = "Türkiye",
                    fontSize = 12.sp,
                    color =
                        Color.White.copy(
                            alpha = 0.80f
                        )
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = temperature,
                        fontSize = 18.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )

                    CircleArrow()
                }
            }
        }
    }
}

// =====================================================
// TÜRKİYE ŞEHİR KARTI
// =====================================================

@Composable
private fun CityListCard(
    city: City,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(
                horizontal = 20.dp,
                vertical = 5.dp
            )
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            CityThumbnail(
                city = city
            )

            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = city.name,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        Color(0xFF101828)
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text = "Türkiye",
                    fontSize = 12.sp,
                    color =
                        Color(0xFF71809A)
                )
            }

            CircleArrow(
                light = true
            )
        }
    }
}

// =====================================================
// ŞEHİR THUMBNAIL
// =====================================================

@Composable
private fun CityThumbnail(
    city: City
) {

    val image =
        cityImageResource(city.name)

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

            DefaultCityIllustration()
        }
    }
}

// =====================================================
// ARAMA SONUÇLARI
// =====================================================

@Composable
private fun SearchResultCard(
    city: City,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(
                horizontal = 20.dp,
                vertical = 6.dp
            )
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(23.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(11.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            CityThumbnail(
                city = city
            )

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = city.name,
                    maxLines = 1,
                    overflow =
                        TextOverflow.Ellipsis,
                    fontSize = 17.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        Color(0xFF101828)
                )

                if (
                    city.subtitle
                        .isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(
                        text = city.subtitle,
                        maxLines = 1,
                        overflow =
                            TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        color =
                            Color(0xFF71809A)
                    )
                }
            }

            CircleArrow(
                light = true
            )
        }
    }
}

// =====================================================
// ARAMA MESAJI
// =====================================================

@Composable
private fun SearchMessageCard(
    title: String,
    message: String
) {

    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(22.dp)
        ) {

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight =
                    FontWeight.Bold,
                color =
                    Color(0xFF101828)
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text = message,
                fontSize = 14.sp,
                color =
                    Color(0xFF71809A)
            )
        }
    }
}

// =====================================================
// BAŞLIK
// =====================================================

@Composable
private fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight =
            FontWeight.Bold,
        color =
            Color(0xFF101828)
    )
}

// =====================================================
// OK İKONU
// =====================================================

@Composable
private fun CircleArrow(
    light: Boolean = false
) {

    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                color =
                    if (light) {
                        Color(0xFFF0F5FB)
                    } else {
                        Color.White.copy(
                            alpha = 0.90f
                        )
                    },

                shape = CircleShape
            ),

        contentAlignment =
            Alignment.Center
    ) {

        androidx.compose.foundation.Canvas(
            modifier =
                Modifier.size(16.dp)
        ) {

            val path = Path()

            path.moveTo(
                size.width * 0.37f,
                size.height * 0.22f
            )

            path.lineTo(
                size.width * 0.67f,
                size.height * 0.50f
            )

            path.lineTo(
                size.width * 0.37f,
                size.height * 0.78f
            )

            drawPath(
                path = path,
                color =
                    Color(0xFF35679F),

                style = Stroke(
                    width =
                        2.2.dp.toPx()
                )
            )
        }
    }
}

// =====================================================
// SEARCH İKONU
// =====================================================

@Composable
private fun SearchIcon(
    color: Color
) {

    androidx.compose.foundation.Canvas(
        modifier =
            Modifier.size(23.dp)
    ) {

        drawCircle(
            color = color,

            radius =
                size.minDimension *
                        0.29f,

            center = Offset(
                size.width * 0.42f,
                size.height * 0.42f
            ),

            style = Stroke(
                width =
                    2.dp.toPx()
            )
        )

        drawLine(
            color = color,

            start = Offset(
                size.width * 0.63f,
                size.height * 0.63f
            ),

            end = Offset(
                size.width * 0.85f,
                size.height * 0.85f
            ),

            strokeWidth =
                2.dp.toPx()
        )
    }
}

// =====================================================
// GÖRSELİ OLMAYAN ŞEHİRLER İÇİN
// =====================================================

@Composable
private fun DefaultCityIllustration() {

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
            )
            .drawBehind {

                val buildingColor =
                    Color(0xFF6487A8)

                drawRect(
                    color =
                        buildingColor.copy(
                            alpha = 0.75f
                        ),

                    topLeft = Offset(
                        size.width * 0.12f,
                        size.height * 0.48f
                    ),

                    size =
                        androidx.compose.ui.geometry.Size(
                            size.width * 0.18f,
                            size.height * 0.40f
                        )
                )

                drawRect(
                    color =
                        buildingColor.copy(
                            alpha = 0.55f
                        ),

                    topLeft = Offset(
                        size.width * 0.36f,
                        size.height * 0.34f
                    ),

                    size =
                        androidx.compose.ui.geometry.Size(
                            size.width * 0.19f,
                            size.height * 0.54f
                        )
                )

                drawRect(
                    color =
                        buildingColor.copy(
                            alpha = 0.65f
                        ),

                    topLeft = Offset(
                        size.width * 0.60f,
                        size.height * 0.55f
                    ),

                    size =
                        androidx.compose.ui.geometry.Size(
                            size.width * 0.26f,
                            size.height * 0.33f
                        )
                )
            }
    )
}

// =====================================================
// ŞEHİR -> DRAWABLE
// =====================================================

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
        "adana" ->
            R.drawable.city_adana

        else -> null
    }
}

// =====================================================
// POPÜLER ŞEHİRLER
// =====================================================

private val popularCities =
    listOf(

        City(
            id = 1001L,
            name = "İstanbul",
            latitude = 41.0082,
            longitude = 28.9784,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "İstanbul"
        ),

        City(
            id = 1002L,
            name = "Ankara",
            latitude = 39.9334,
            longitude = 32.8597,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Ankara"
        ),

        City(
            id = 1003L,
            name = "İzmir",
            latitude = 38.4237,
            longitude = 27.1428,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "İzmir"
        ),

        City(
            id = 1004L,
            name = "Bursa",
            latitude = 40.1950,
            longitude = 29.0600,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Bursa"
        )
    )

// =====================================================
// TÜRKİYE ŞEHİRLERİ
// =====================================================

private val turkeyCities =
    listOf(

        City(
            id = 2001L,
            name = "Adana",
            latitude = 37.0000,
            longitude = 35.3213,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Adana"
        ),

        City(
            id = 2002L,
            name = "Ankara",
            latitude = 39.9334,
            longitude = 32.8597,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Ankara"
        ),

        City(
            id = 2003L,
            name = "Antalya",
            latitude = 36.8969,
            longitude = 30.7133,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Antalya"
        ),

        City(
            id = 2004L,
            name = "Balıkesir",
            latitude = 39.6484,
            longitude = 27.8826,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Balıkesir"
        ),

        City(
            id = 2005L,
            name = "Bursa",
            latitude = 40.1950,
            longitude = 29.0600,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Bursa"
        ),

        City(
            id = 2006L,
            name = "Çanakkale",
            latitude = 40.1553,
            longitude = 26.4142,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Çanakkale"
        ),

        City(
            id = 2007L,
            name = "Denizli",
            latitude = 37.7765,
            longitude = 29.0864,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Denizli"
        ),

        City(
            id = 2008L,
            name = "Eskişehir",
            latitude = 39.7767,
            longitude = 30.5206,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Eskişehir"
        ),

        City(
            id = 2009L,
            name = "Gaziantep",
            latitude = 37.0662,
            longitude = 37.3833,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Gaziantep"
        ),

        City(
            id = 2010L,
            name = "İstanbul",
            latitude = 41.0082,
            longitude = 28.9784,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "İstanbul"
        ),

        City(
            id = 2011L,
            name = "İzmir",
            latitude = 38.4237,
            longitude = 27.1428,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "İzmir"
        ),

        City(
            id = 2012L,
            name = "Kayseri",
            latitude = 38.7225,
            longitude = 35.4875,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Kayseri"
        ),

        City(
            id = 2013L,
            name = "Konya",
            latitude = 37.8746,
            longitude = 32.4932,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Konya"
        ),

        City(
            id = 2014L,
            name = "Mersin",
            latitude = 36.8121,
            longitude = 34.6415,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Mersin"
        ),

        City(
            id = 2015L,
            name = "Samsun",
            latitude = 41.2867,
            longitude = 36.3300,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Samsun"
        ),

        City(
            id = 2016L,
            name = "Trabzon",
            latitude = 41.0015,
            longitude = 39.7178,
            countryCode = "TR",
            country = "Türkiye",
            admin1 = "Trabzon"
        )
    )