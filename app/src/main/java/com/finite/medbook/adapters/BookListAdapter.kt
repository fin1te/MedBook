package com.finite.medbook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finite.medbook.R
import com.finite.medbook.data.model.Book
import com.finite.medbook.databinding.ItemBookBinding
import com.finite.medbook.ui.main.BookViewModel

class BookListAdapter(private val viewModel: BookViewModel) : ListAdapter<Book, BookListAdapter.BookViewHolder>(BookDiffCallback()) {

    private val bookmarkedBooksSet = mutableSetOf<String>()

    fun updateBookmarks(bookmarks: Set<String>) {
        bookmarkedBooksSet.clear()
        bookmarkedBooksSet.addAll(bookmarks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

    inner class BookViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val book = getItem(adapterPosition)
                val bundle = bundleOf("selectedBook" to book)

                val navController = Navigation.findNavController(itemView)
                navController.navigate(R.id.action_bookFragment_to_bookDetailFragment, bundle)
            }

            binding.bookmarkImageView.setOnClickListener {
                val book = getItem(adapterPosition)
                //viewModel.toggleBookmark(book, viewModel.getCurrentUser())
                viewModel.toggleBookmark(book, viewModel.getCurrentUser())
            }
        }


        fun bind(book: Book) {
            binding.titleTextView.text = book.title
            binding.hitsTextView.text = "Hits: ${book.hits}"

            Glide.with(itemView)
                .load(book.image)
                .placeholder(R.drawable.logo_medbook)
                .error(R.drawable.logo_medbook)
                .centerCrop()
                .into(binding.thumbnailImageView)

            if (viewModel.isBookmarked(book.id)) {
                binding.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_saved)
            } else {
                binding.bookmarkImageView.setImageResource(R.drawable.ic_bookmark)
            }

        }
    }

    private class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}