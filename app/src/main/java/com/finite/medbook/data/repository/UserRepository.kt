package com.finite.medbook.data.repository

import android.content.Context
import com.finite.medbook.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<Pair<String, String>>
)

class UserRepository(private val context: Context) {

    private val errors = mutableListOf<Pair<String, String>>()

    // Checks is the Registration form is valid, if not, returns a list of errors
    fun validateRegistration(
        name: String,
        password: String,
        confirmPassword: String,
        country: String
    ): ValidationResult {

        if(errors.isNotEmpty()) {
            errors.clear()
        }

        validName(name)
        validPassword(password)
        validConfirmPassword(password, confirmPassword)
        isCountrySelected(country)

        return ValidationResult(errors.isEmpty(), errors)
    }

    // Checks is the Login form is valid, if not, returns a list of errors
    fun validateLogin(name: String, password: String): ValidationResult {
        if(errors.isNotEmpty()) {
            errors.clear()
        }

        validName(name)
        validPassword(password)

        return ValidationResult(errors.isEmpty(), errors)
    }

    // Checks if the user exists in the database
    fun checkUserExists(name: String, password: String): ValidationResult {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userListJson = sharedPreferences.getString("user_list", "[]")
        val userListType = object : TypeToken<List<User>>() {}.type
        val userList = Gson().fromJson<List<User>>(userListJson, userListType).toMutableList()

        for (user in userList) {
            if (user.name == name && user.password == password) {
                return ValidationResult(true, emptyList())
            }
        }

        return ValidationResult(false, listOf(Pair("name", "Invalid username or password")))
    }

    // Saves the user details to the database
    fun saveUserDetails(username: String, password: String, country: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userListJson = sharedPreferences.getString("user_list", "[]")
        val userListType = object : TypeToken<List<User>>() {}.type
        val userList = Gson().fromJson<List<User>>(userListJson, userListType).toMutableList()

        userList.add(User(username, password, country))

        val editor = sharedPreferences.edit()
        editor.putString("user_list", Gson().toJson(userList))
        editor.apply()
    }

    // Saves the current user to the database
    fun saveCurrentUser(username: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("current_user", username)
        editor.apply()
    }



    // Checks if the name is valid
    private fun validName(name: String): Boolean {
        return if (name.isEmpty()) {
            errors.add(Pair("name", "Name is required"))
            false
        } else if (name.contains(Regex("[0-9]"))) {
            errors.add(Pair("name", "Name cannot contain numbers"))
            false
        } else {
            true
        }
    }

    // Checks if the password is valid and meets the all requirements
    private fun validPassword(password: String): Boolean {
        val passwordPattern =
            Regex("^(?=.*[0-9])(?=.*[!@#\$%&()\\[\\]])(?=.*[a-z])(?=.*[A-Z]).{8,}$")

        if (password.isEmpty()) {
            errors.add(Pair("password", "Password is required"))
            return false
        }

        if (!password.matches(passwordPattern)) {
            if (!password.matches(Regex(".*[0-9].*"))) {
                errors.add(Pair("password", "Password must contain at least one digit"))
            } else if (!password.matches(Regex(".*[!@#\$%&()].*"))) {
                errors.add(Pair("password", "Password must contain at least one special character"))
            } else if (!password.matches(Regex(".*[a-z].*"))) {
                errors.add(Pair("password", "Password must contain at least one lowercase letter"))
            } else if (!password.matches(Regex(".*[A-Z].*"))) {
                errors.add(Pair("password", "Password must contain at least one uppercase letter"))
            } else {
                errors.add(Pair("password", "Password must have at least 8 characters"))
            }
            return false
        }

        return true
    }

    // Checks if the confirm password is valid and matches the password
    private fun validConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (confirmPassword.isEmpty()) {
            errors.add(Pair("confirmPassword", "Confirm password is required"))
            false
        } else if (confirmPassword != password) {
            errors.add(Pair("confirmPassword", "Passwords do not match"))
            false
        } else {
            true
        }
    }

    // Checks if the country is selected
    private fun isCountrySelected(country: String): Boolean {
        return if (country.isEmpty()) {
            errors.add(Pair("country", "Country is required"))
            false
        } else {
            true
        }
    }

    // Checks if the user is logged in
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentUser = sharedPreferences.getString("current_user", "")
        return currentUser != ""
    }

}

