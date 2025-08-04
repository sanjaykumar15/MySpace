package com.sanjay.myspace.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanjay.myspace.R
import com.sanjay.myspace.ui.component.ClickableTextView
import com.sanjay.myspace.ui.component.PasswordFieldWithLabel
import com.sanjay.myspace.ui.component.ProgressDialog
import com.sanjay.myspace.ui.component.TextFieldWithLabel
import com.sanjay.myspace.ui.component.ThemeButton
import com.sanjay.myspace.ui.component.TopBarComp
import com.sanjay.myspace.ui.event.LoginEvents
import com.sanjay.myspace.ui.state.LoginState
import com.sanjay.myspace.ui.theme.MySpaceTheme
import com.sanjay.myspace.ui.theme.White

@Composable
fun LoginScreen(
    state: LoginState,
    onEvent: (LoginEvents) -> Unit,
) {
    if (state.isLoading) {
        ProgressDialog(message = state.loadingMsg.ifEmpty { null })
    }

    BackHandler {
        if (state.isSignUp) {
            onEvent(LoginEvents.HandleSignup)
        } else {
            onEvent(LoginEvents.OnBackPressed)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopBarComp(
                title = if (!state.isSignUp) stringResource(R.string.login)
                else stringResource(R.string.sign_up),
                showBackIcon = false,
                onBackClick = {}
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = White
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                TextFieldWithLabel(
                    label = stringResource(R.string.email),
                    value = state.username,
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    onValueChange = {
                        onEvent(LoginEvents.OnUserNameChanged(it))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.username)
                        )
                    }
                )

                PasswordFieldWithLabel(
                    label = stringResource(R.string.password),
                    value = state.password,
                    modifier = Modifier
                        .fillMaxWidth(),
                    imeAction = ImeAction.Done,
                    onValueChange = {
                        onEvent(LoginEvents.OnPasswordChanged(it))
                    }
                )

                ThemeButton(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    text = if (state.isSignUp) stringResource(R.string.sign_up)
                    else stringResource(R.string.login),
                    onClick = {
                        onEvent(LoginEvents.OnLoginClicked)
                    }
                )

                ClickableTextView(
                    clickableTextData = if (state.isSignUp) state.loginText
                    else state.signUpText,
                    modifier = Modifier
                        .align(Alignment.End),
                    onTextClicked = {
                        onEvent(LoginEvents.HandleSignup)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MySpaceTheme {
        LoginScreen(
            state = LoginState(),
            onEvent = { }
        )
    }
}