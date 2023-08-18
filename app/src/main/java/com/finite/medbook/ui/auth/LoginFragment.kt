package com.finite.medbook.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finite.medbook.data.repository.UserRepository
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

            val name = binding.nameTextInput.editText?.text.toString().trim()
            val password = binding.passwordTextInput.editText?.text.toString()

            val result = UserRepository().validateLogin(name, password)

            if(result.isValid) {
                binding.passwordTextInput.clearFocus()
                clearAllErrors()
                hideKeyboard()
                Snackbar.make(
                    requireView(),
                    "Login successful",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                result.errors.forEach { error ->
                    when(error.first) {
                        "name" -> binding.nameTextInput.error = error.second
                        "password" -> binding.passwordTextInput.error = error.second
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
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}