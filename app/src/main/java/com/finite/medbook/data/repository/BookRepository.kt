package com.finite.medbook.data.repository

import android.content.Context
import com.finite.medbook.data.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class BookRepository(private val context: Context) {

    fun getBooksFromJson(): List<Book> {
        val inputStream = context.assets.open("booklist.json")
        val reader = InputStreamReader(inputStream)
        val listType = object : TypeToken<List<Book>>() {}.type
        return Gson().fromJson(reader, listType)
    }
}