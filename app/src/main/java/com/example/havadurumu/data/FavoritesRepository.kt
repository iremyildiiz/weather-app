package com.example.havadurumu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.havadurumu.model.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "favorites"
)

class FavoritesRepository(
    private val context: Context
) {

    private val gson = Gson()

    private val favoritesKey =
        stringPreferencesKey("favorite_cities")

    val favorites: Flow<List<City>> =
        context.dataStore.data.map { preferences ->

            val json =
                preferences[favoritesKey]

            if (json.isNullOrBlank()) {
                emptyList()
            } else {

                try {

                    val type =
                        object :
                            TypeToken<List<City>>() {}.type

                    gson.fromJson<List<City>>(
                        json,
                        type
                    ) ?: emptyList()

                } catch (e: Exception) {
                    emptyList()
                }
            }
        }

    suspend fun saveFavorites(
        cities: List<City>
    ) {

        val json =
            gson.toJson(cities)

        context.dataStore.edit { preferences ->

            preferences[favoritesKey] =
                json
        }
    }
}