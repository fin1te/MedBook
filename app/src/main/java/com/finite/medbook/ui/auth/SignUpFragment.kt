package com.finite.medbook.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finite.medbook.R
import com.finite.medbook.data.repository.CountryRepository
import com.finite.medbook.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCountryDropdown()

        binding.goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.countryDropdown.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
            }
        }

    }

    private fun initCountryDropdown() {
        val countryRepository = CountryRepository(requireContext())
        val countries = countryRepository.getCountriesFromJson()

        val countryList = countries.map { it.value.country }.sorted()
        val countryAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countryList)
        binding.countryDropdown.setAdapter(countryAdapter)
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}