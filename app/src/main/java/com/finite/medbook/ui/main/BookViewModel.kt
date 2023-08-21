package com.finite.medbook.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finite.medbook.data.model.Book
import com.finite.medbook.data.repository.BookRepository
import com.finite.medbook.databinding.FragmentBookBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val bookRepository = BookRepository(application.applicationContext)

    private val _booksLiveData = MutableLiveData<List<Book>>()
    val booksLiveData: LiveData<List<Book>> = _booksLiveData

    private val _bookmarksLiveData = MutableLiveData<Set<String>>()
    val bookmarksLiveData: LiveData<Set<String>> = _bookmarksLiveData

    private val bookmarkedBooksSet = mutableSetOf<String>()

    init {
        loadBookmarksFromPreferences(getCurrentUser())
        bookmarkedBooksSet.addAll(loadBookmarksFromPreferences(getCurrentUser()))
        val currentUser = getCurrentUser()
        loadUserBookmarks(currentUser)
    }

    fun getCurrentUser(): String {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("current_user", "") ?: ""
    }

    fun loadBooks() {
        val books = bookRepository.getBooksFromJson()
        val sortedBooks = books.sortedBy { it.title.lowercase() }
        _booksLiveData.value = sortedBooks
    }

    private fun loadUserBookmarks(currentUser: String) {
        val bookmarksSet = loadBookmarksFromPreferences(currentUser)
        bookmarkedBooksSet.clear()
        bookmarkedBooksSet.addAll(bookmarksSet)
        _bookmarksLiveData.value = bookmarkedBooksSet
    }

    fun toggleBookmark(book: Book, currentUser: String) {
        val bookId = book.id
        if (bookId in bookmarkedBooksSet) {
            bookmarkedBooksSet.remove(bookId)
        } else {
            bookmarkedBooksSet.add(bookId)
        }
        _bookmarksLiveData.value = bookmarkedBooksSet

        saveBookmarksToPreferences(currentUser)
    }

    fun sortBooksByTitleAscending() {
        val sortedBooks = _booksLiveData.value?.sortedBy { it.title }
        _booksLiveData.value = sortedBooks!!
    }

    fun sortBooksByTitleDescending() {
        val sortedBooks = _booksLiveData.value?.sortedByDescending { it.title }
        _booksLiveData.value = sortedBooks!!

    }

    fun sortBooksByHitsDescending() {
        _booksLiveData.value = _booksLiveData.value?.sortedByDescending { it.hits }
    }

    fun sortBooksByHitsAscending() {
        _booksLiveData.value = _booksLiveData.value?.sortedBy { it.hits }
    }

    fun isBookmarked(bookId: String): Boolean {
        return bookId in bookmarkedBooksSet
    }

    private fun saveBookmarksToPreferences(username: String) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("bookmarks_$username", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("bookmarks_set", bookmarkedBooksSet)
        editor.apply()
    }

    private fun loadBookmarksFromPreferences(username: String): Set<String> {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("bookmarks_$username", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("bookmarks_set", emptySet()) ?: emptySet()
    }

}