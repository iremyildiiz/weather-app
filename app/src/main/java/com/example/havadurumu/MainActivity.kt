package com.example.havadurumu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.havadurumu.ui.HavaNavHost
import com.example.havadurumu.ui.theme.HavaDurumuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        window.statusBarColor =
            Color.Transparent.toArgb()

        window.navigationBarColor =
            Color.Transparent.toArgb()

        setContent {

            HavaDurumuTheme {

                HavaNavHost()
            }
        }
    }
}