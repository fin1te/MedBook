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

            if (validFields()) {
                Snackbar.make(
                    requireView(),
                    "Sign up successful",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    requireView(),
                    "Sign up failed",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validFields(): Boolean {
        val name = binding.nameTextInput.editText?.text.toString().trim()
        val password = binding.passwordTextInput.editText?.text.toString()
        val confirmPassword = binding.confirmPasswordTextInput.editText?.text.toString()
        val countryDropDown = binding.countryDropdown.text.toString()

        val nameValid = validName(name)
        val passwordValid = validPassword(password)
        val confirmPasswordValid = validConfirmPassword(password, confirmPassword)
        val countrySelected = isCountrySelected(countryDropDown)

        return nameValid && passwordValid && confirmPasswordValid && countrySelected
    }

    private fun isCountrySelected(countryDropDown: String): Boolean {
        return if (countryDropDown.isEmpty()) {
            binding.countryMenu.error = "Country is required"
            false
        } else {
            binding.countryMenu.error = null
            binding.countryMenu.isErrorEnabled = false
            true
        }
    }

    private fun validName(name: String): Boolean {
        return if (name.isEmpty()) {
            binding.nameTextInput.error = "Name is required"
            false
        } else if (name.contains(Regex("[0-9]"))) {
            binding.nameTextInput.error = "Name cannot contain numbers"
            false
        } else {
            binding.nameTextInput.error = null
            binding.nameTextInput.isErrorEnabled = false
            true
        }
    }

    private fun validPassword(password: String): Boolean {
        val passwordPattern =
            Regex("^(?=.*[0-9])(?=.*[!@#\$%&()\\[\\]])(?=.*[a-z])(?=.*[A-Z]).{8,}$")

        if (password.isEmpty()) {
            binding.passwordTextInput.error = "Password is required"
            return false
        }

        if (!password.matches(passwordPattern)) {
            if (!password.matches(Regex(".*[0-9].*"))) {
                binding.passwordTextInput.error = "Password must contain at least one digit"
            } else if (!password.matches(Regex(".*[!@#\$%&()].*"))) {
                binding.passwordTextInput.error =
                    "Password must contain at least one special character"
            } else if (!password.matches(Regex(".*[a-z].*"))) {
                binding.passwordTextInput.error =
                    "Password must contain at least one lowercase letter"
            } else if (!password.matches(Regex(".*[A-Z].*"))) {
                binding.passwordTextInput.error =
                    "Password must contain at least one uppercase letter"
            } else {
                binding.passwordTextInput.error = "Password must have at least 8 characters"
            }
            return false
        }

        binding.passwordTextInput.error = null
        binding.passwordTextInput.isErrorEnabled = false
        return true
    }


    private fun validConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (confirmPassword.isEmpty()) {
            binding.confirmPasswordTextInput.error = "Confirm password is required"
            false
        } else if (confirmPassword != password) {
            binding.confirmPasswordTextInput.error = "Passwords do not match"
            false
        } else {
            binding.confirmPasswordTextInput.error = null
            binding.confirmPasswordTextInput.isErrorEnabled = false
            true
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