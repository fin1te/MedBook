package com.finite.medbook.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.finite.medbook.R
import com.finite.medbook.adapters.BookListAdapter
import com.finite.medbook.databinding.FragmentBookBinding
import com.google.android.material.snackbar.Snackbar

class BookFragment : Fragment() {

    private lateinit var binding: FragmentBookBinding
    private val viewModel: BookViewModel by activityViewModels()
    private lateinit var adapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeAppOnBackPress()
        setupSortDropdown()
        setupRecyclerView()

        binding.logoutButton.setOnClickListener {
            val sharedPreferences =
                requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().remove("current_user").apply()
            Snackbar.make(
                requireView(),
                getString(R.string.logged_out_successfully), Snackbar.LENGTH_SHORT
            ).show()
//            viewModel.logout()
            findNavController().popBackStack()
        }


//        Toast.makeText(context, viewModel.getCurrentUser(), Toast.LENGTH_SHORT).show()


    }

    private fun setupRecyclerView() {
        adapter = BookListAdapter(viewModel)
        binding.bookRecyclerView.adapter = adapter

        viewModel.booksLiveData.observe(viewLifecycleOwner) { books ->
            adapter.submitList(null)
            adapter.submitList(books)
            binding.bookRecyclerView.scrollToPosition(0)
        }

        viewModel.bookmarksLiveData.observe(viewLifecycleOwner) { bookmarks ->
            adapter.updateBookmarks(bookmarks)
        }

        viewModel.loadBooks()

    }


    private fun setupSortDropdown() {
        val sortOptions = initDropdownMenu()

        val defaultSelection = getString(R.string.sort_title_a_z)
        binding.sortOptionsDropdown.setText(defaultSelection, false)

        binding.sortOptionsDropdown.setOnItemClickListener { _, _, position, _ ->
            when (sortOptions[position]) {
                getString(R.string.sort_title_a_z) -> viewModel.sortBooksByTitleAscending()
                getString(R.string.sort_title_z_a) -> viewModel.sortBooksByTitleDescending()
                getString(R.string.sort_hits_highest) -> viewModel.sortBooksByHitsDescending()
                getString(R.string.sort_hits_lowest) -> viewModel.sortBooksByHitsAscending()
            }
        }
    }

    private fun initDropdownMenu(): Array<String> {
        val sortOptions = resources.getStringArray(R.array.sort_options_array)
        val sortAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            sortOptions
        )
        binding.sortOptionsDropdown.setAdapter(sortAdapter)

        return sortOptions
    }

    private fun closeAppOnBackPress() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                requireActivity().finish()
                true
            } else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initDropdownMenu()
    }


    //    private fun getCurrentUser() {
//        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//        val currentUser = sharedPreferences.getString("current_user", "")
//        binding.welcomeTextView.text = getString(R.string.welcomeText, currentUser)
//    }


}