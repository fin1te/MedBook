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
import com.finite.medbook.data.repository.UserRepository
import com.finite.medbook.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initCountryDropdown()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            clearAllErrors()
        }

        binding.countryDropdown.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideKeyboard()
                binding.countryMenu.error = null
                binding.countryMenu.isErrorEnabled = false
            }
        }

        binding.nameTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.nameTextInput.error = null
                binding.nameTextInput.isErrorEnabled = false
            }
        }

        binding.passwordTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordTextInput.error = null
                binding.passwordTextInput.isErrorEnabled = false
            }
        }

        binding.confirmPasswordTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.confirmPasswordTextInput.error = null
                binding.confirmPasswordTextInput.isErrorEnabled = false
            }
        }

        binding.signUpButton.setOnClickListener {

            binding.nameTextInput.clearFocus()
            binding.passwordTextInput.clearFocus()
            binding.confirmPasswordTextInput.clearFocus()
            binding.countryDropdown.clearFocus()

            val name = binding.nameTextInput.editText?.text.toString().trim()
            val password = binding.passwordTextInput.editText?.text.toString()
            val confirmPassword = binding.confirmPasswordTextInput.editText?.text.toString()
            val country = binding.countryDropdown.text.toString()

            val result = UserRepository().validateRegistration(
                name, password, confirmPassword, country
            )

            if (result.isValid) {
                clearAllErrors()
                Snackbar.make(requireView(), "Sign up successful", Snackbar.LENGTH_SHORT).show()
            } else {
                result.errors.forEach {
                    when (it.first) {
                        "name" -> binding.nameTextInput.error = it.second
                        "password" -> binding.passwordTextInput.error = it.second
                        "confirmPassword" -> binding.confirmPasswordTextInput.error = it.second
                        "country" -> binding.countryMenu.error = it.second
                    }
                }
            }
        }
    }

    private fun clearAllErrors() {
        binding.nameTextInput.error = null
        binding.nameTextInput.isErrorEnabled = false
        binding.passwordTextInput.error = null
        binding.passwordTextInput.isErrorEnabled = false
        binding.confirmPasswordTextInput.error = null
        binding.confirmPasswordTextInput.isErrorEnabled = false
        binding.countryMenu.error = null
        binding.countryMenu.isErrorEnabled = false
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