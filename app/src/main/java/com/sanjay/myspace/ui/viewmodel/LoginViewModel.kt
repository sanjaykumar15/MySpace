package com.sanjay.myspace.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjay.myspace.helper.DataStoreHelper
import com.sanjay.myspace.helper.DataStoreKey
import com.sanjay.myspace.helper.FirebaseHelper
import com.sanjay.myspace.ui.event.LoginEvents
import com.sanjay.myspace.ui.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseHelper: FirebaseHelper,
    private val datastoreHelper: DataStoreHelper,
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun eventHandler(event: LoginEvents) {
        when (event) {
            is LoginEvents.OnUserNameChanged -> {
                state = state.copy(
                    username = event.username
                )
            }

            is LoginEvents.OnPasswordChanged -> {
                state = state.copy(
                    password = event.password
                )
            }

            LoginEvents.DismissLoading -> {
                state = state.copy(
                    isLoading = false,
                    loadingMsg = ""
                )
            }

            LoginEvents.HandleSignup -> {
                state = state.copy(
                    isSignUp = !state.isSignUp
                )
            }

            else -> {}
        }
    }

    fun validation(): String {
        return when {
            state.username.trim().isEmpty() -> "Enter Username"
            state.password.isEmpty() -> "Enter Password"
            state.password.length < 6 -> "Password must be at least 6 characters"
            else -> ""
        }
    }

    fun login(
        onLoginRes: (Boolean, String?) -> Unit,
    ) {
        state = state.copy(
            isLoading = true,
            loadingMsg = "Logging In..."
        )
        firebaseHelper.login(
            username = state.username,
            password = state.password,
            onLoginRes = { result, userId, message ->
                viewModelScope.launch {
                    datastoreHelper.saveString(DataStoreKey.USER_ID, userId)
                    datastoreHelper.saveString(DataStoreKey.USER_EMAIL, state.username)
                }
                onLoginRes(result, message)
            }
        )
    }

    fun signUp(
        onSignUpRes: (Boolean, String?) -> Unit,
    ) {
        state = state.copy(
            isLoading = true,
            loadingMsg = "Creating Account..."
        )
        firebaseHelper.signUp(
            username = state.username,
            password = state.password,
            onSignUpRes = { result, userId, message ->
                viewModelScope.launch {
                    datastoreHelper.saveString(DataStoreKey.USER_ID, userId)
                    datastoreHelper.saveString(DataStoreKey.USER_EMAIL, state.username)
                }
                onSignUpRes(result, message)
            }
        )
    }
}