package com.sanjay.myspace.ui.event

sealed class LoginEvents {

    data object OnLoginClicked : LoginEvents()
    data object HandleSignup : LoginEvents()
    data object OnBackPressed : LoginEvents()
    data object DismissLoading : LoginEvents()

    data class OnUserNameChanged(
        val username: String,
    ) : LoginEvents()

    data class OnPasswordChanged(
        val password: String,
    ) : LoginEvents()

}