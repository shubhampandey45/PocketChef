package com.sp45.pocketchef.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sp45.pocketchef.domain.AuthResult
import com.sp45.pocketchef.domain.UserProfile
import com.sp45.pocketchef.domain.repository.AuthRepository
import com.sp45.pocketchef.domain.repository.UserRepository
import com.sp45.pocketchef.util.Resource
import com.sp45.pocketchef.util.ValidationUtil
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        emailError = null
                    )
                }
            }

            is AuthEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password,
                        passwordError = null
                    )
                }
            }

            is AuthEvent.ConfirmPasswordChanged -> {
                _state.update {
                    it.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordError = null
                    )
                }
            }

            AuthEvent.ToggleAuthMode -> {
                _state.update {
                    it.copy(
                        isLoginMode = !it.isLoginMode,
                        emailError = null,
                        passwordError = null,
                        confirmPasswordError = null,
                        errorMessage = null,
                        // Clear fields when switching modes
                        email = "",
                        password = "",
                        confirmPassword = ""
                    )
                }
            }

            AuthEvent.Login -> {
                login()
            }

            AuthEvent.Register -> {
                register()
            }

            is AuthEvent.ResetPassword -> {
                resetPassword(event.email)
            }

            AuthEvent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun login() {
        val email = _state.value.email
        val password = _state.value.password

        val emailError = ValidationUtil.validateEmail(email)
        val passwordError = ValidationUtil.validatePassword(password)

        if (emailError != null || passwordError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            authRepository.loginUser(email, password).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = formatErrorMessage(result.message)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun register() {
        val email = _state.value.email
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword

        val emailError = ValidationUtil.validateEmail(email)
        val passwordError = ValidationUtil.validatePassword(password)
        val confirmPasswordError = ValidationUtil.validateConfirmPassword(password, confirmPassword)

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            authRepository.registerUser(email, password).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Success -> {
                        // Create user profile after successful registration
                        val userProfile = UserProfile(
                            userId = result.userId,
                            email = email,
                            fullName = "", // You might want to collect this during registration
                            profilePhotoUrl = "",
                            createdAt = System.currentTimeMillis()
                        )

                        // Save profile to Firestore
                        viewModelScope.launch {
                            userRepository.saveUserProfile(userProfile).collect { saveResult ->
                                when (saveResult) {
                                    is Resource.Success -> {
                                        _state.update {
                                            it.copy(
                                                isLoading = false,
                                                isSuccess = true,
                                                errorMessage = null
                                            )
                                        }
                                    }
                                    is Resource.Error -> {
                                        _state.update {
                                            it.copy(
                                                isLoading = false,
                                                errorMessage = "Account created but failed to save profile"
                                            )
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = formatErrorMessage(result.message)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "✓ Password reset email sent! Check your inbox."
                            )
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = formatErrorMessage(result.message)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Format Firebase error messages to be more user-friendly
     */
    private fun formatErrorMessage(message: String): String {
        return when {
            message.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                "Invalid email or password."
            message.contains("INVALID_EMAIL", ignoreCase = true) ->
                "Invalid email format."
            message.contains("USER_NOT_FOUND", ignoreCase = true) ->
                "No account found with this email."
            message.contains("WRONG_PASSWORD", ignoreCase = true) ->
                "Incorrect password."
            message.contains("EMAIL_ALREADY_IN_USE", ignoreCase = true) ->
                "An account with this email already exists."
            message.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "Password should be at least 6 characters."
            message.contains("NETWORK", ignoreCase = true) ->
                "Network error. Please check your connection."
            else -> "An error occurred. Please try again."
        }
    }
}