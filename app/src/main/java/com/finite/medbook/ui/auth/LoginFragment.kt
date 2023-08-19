package com.finite.medbook.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.finite.medbook.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearErrorsOnFocusChange()

        viewModel.loginResult.observe(viewLifecycleOwner) { validationResult ->
            if (validationResult.isValid) {
                binding.passwordTextInput.clearFocus()
                viewModel.clearAllErrors(binding)
                viewModel.hideKeyboard(requireContext(), requireView())

                val isValidUser = viewModel.checkUserExists(
                    binding.nameTextInput.editText?.text.toString().trim(),
                    binding.passwordTextInput.editText?.text.toString()
                )

                if(isValidUser) {
                    Snackbar.make(requireView(), "Login successful", Snackbar.LENGTH_SHORT).show()
                }

            } else if (validationResult.errors.isEmpty()) {
                viewModel.clearAllErrors(binding)
            } else {
                validationResult.errors.forEach {
                    when (it.first) {
                        "name" -> binding.nameTextInput.error = it.second
                        "password" -> binding.passwordTextInput.error = it.second
                    }
                }
            }
        }

        binding.loginButton.setOnClickListener {
            binding.nameTextInput.clearFocus()

            viewModel.validateAndLogin(
                binding.nameTextInput.editText?.text.toString().trim(),
                binding.passwordTextInput.editText?.text.toString()
            )
        }

        binding.goToSignUpButton.setOnClickListener {
            findNavController().popBackStack()
            viewModel.clearValidationResult()
        }
    }

    private fun clearErrorsOnFocusChange() {
        binding.nameTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.clearErrors(binding, "name")
            }
        }

        binding.passwordTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.clearErrors(binding, "password")
            }
        }
    }
}