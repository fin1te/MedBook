package com.finite.medbook.data.model
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Book(
    val alias: String,
    val hits: Int,
    val id: String,
    val image: String,
    val lastChapterDate: Int,
    val title: String
) : Parcelable