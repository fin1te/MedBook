package com.finite.medbook.ui.main

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.finite.medbook.R
import com.finite.medbook.data.model.Book
import com.finite.medbook.databinding.FragmentBookDetailBinding

class BookDetailFragment : Fragment() {

    private lateinit var binding: FragmentBookDetailBinding
    private val viewModel: BookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the selected book from arguments
        val args = arguments
        val selectedBook: Book? = args?.getParcelable("selectedBook")

        // Display the selected book's details
        selectedBook?.let {
            binding.bookTitle.text = it.title
            binding.bookHits.text = "Hits: ${it.hits}"
            binding.bookAlias.text = "Alias: ${it.alias}"
            binding.bookDescription.text = getString(R.string.book_description)

            
            Glide.with(requireContext())
                .load(it.image)
                .placeholder(R.drawable.logo_medbook)
                .error(R.drawable.logo_medbook)
                .centerCrop()
                .into(binding.bookImage)
        }

        val isBookmarked = viewModel.isBookmarked(selectedBook?.id!!)


        val bookmarkDrawable = if (isBookmarked) {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark_saved)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark)
        }
        binding.bookmarkImageView.setImageDrawable(bookmarkDrawable)


        binding.bookmarkImageView.setOnClickListener {
            viewModel.toggleBookmark(selectedBook, viewModel.getCurrentUser())

            // Update the bookmark icon after toggling
            val newIsBookmarked = viewModel.isBookmarked(selectedBook.id)
            val newBookmarkDrawable = if (newIsBookmarked) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark_saved)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark)
            }
            binding.bookmarkImageView.setImageDrawable(newBookmarkDrawable)
        }
    }


}