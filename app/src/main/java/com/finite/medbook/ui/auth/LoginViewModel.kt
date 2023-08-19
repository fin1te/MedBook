package com.finite.medbook.ui.auth

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finite.medbook.data.repository.UserRepository
import com.finite.medbook.data.repository.ValidationResult
import com.finite.medbook.databinding.FragmentLoginBinding

class LoginViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _loginResult = MutableLiveData<ValidationResult>()
    val loginResult: LiveData<ValidationResult> = _loginResult

    fun validateAndLogin(name: String, password: String) {
        val validationResult = userRepository.validateLogin(name, password)
        _loginResult.value = validationResult
    }

    fun clearValidationResult() {
        _loginResult.value = ValidationResult(false, emptyList())
    }

    fun clearErrors(binding: FragmentLoginBinding, vararg fields: String) {
        fields.forEach {
            when (it) {
                "name" -> {
                    binding.nameTextInput.error = null
                    binding.nameTextInput.isErrorEnabled = false
                }
                "password" -> {
                    binding.passwordTextInput.error = null
                    binding.passwordTextInput.isErrorEnabled = false
                }
            }
        }
    }

    fun clearAllErrors(binding: FragmentLoginBinding) {
        binding.nameTextInput.error = null
        binding.nameTextInput.isErrorEnabled = false
        binding.passwordTextInput.error = null
        binding.passwordTextInput.isErrorEnabled = false
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}