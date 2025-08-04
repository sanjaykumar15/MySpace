package com.sanjay.myspace.ui.state

import com.sanjay.myspace.ui.component.ClickableTextData
import com.sanjay.myspace.ui.theme.TextDark

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loadingMsg: String = "",
    val isSignUp: Boolean = false,
) {
    val signUpText: List<ClickableTextData> = listOf(
        ClickableTextData(
            text = "Don't have an account? ",
            value = "Sign Up",
            textLinkColor = TextDark
        ),
        ClickableTextData(
            text = "Sign Up",
            value = "Sign Up",
            clickable = true,
        )
    )
    val loginText: List<ClickableTextData> = listOf(
        ClickableTextData(
            text = "Have an account? ",
            value = "Login",
            textLinkColor = TextDark
        ),
        ClickableTextData(
            text = "Login",
            value = "Login",
            clickable = true,
        )
    )
}