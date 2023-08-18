package com.finite.medbook.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finite.medbook.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToSignUpButton.setOnClickListener {
            findNavController().popBackStack()
            clearAllErrors()
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

        binding.loginButton.setOnClickListener {
            binding.nameTextInput.clearFocus()
            binding.passwordTextInput.clearFocus()

            if (validFields()) {
                Snackbar.make(
                    requireView(),
                    "Login successful",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    requireView(),
                    "Login failed",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validFields(): Boolean {
        val name = binding.nameTextInput.editText?.text.toString().trim()
        val password = binding.passwordTextInput.editText?.text.toString()

        val nameValid = validName(name)
        val passwordValid = validPassword(password)

        return nameValid && passwordValid
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

    private fun clearAllErrors() {
        binding.nameTextInput.error = null
        binding.nameTextInput.isErrorEnabled = false
        binding.passwordTextInput.error = null
        binding.passwordTextInput.isErrorEnabled = false
    }
}