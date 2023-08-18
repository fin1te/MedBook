package com.finite.medbook.data.repository

import android.content.Context
import com.finite.medbook.data.model.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class CountryRepository(private val context: Context) {
    fun getCountriesFromJson(): Map<String, Country> {
        val inputStream = context.assets.open("countries.json")
        val reader = InputStreamReader(inputStream)
        val listType = object : TypeToken<Map<String, Country>>() {}.type
        return Gson().fromJson(reader, listType)
    }
}