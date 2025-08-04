package com.sanjay.myspace.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.sanjay.myspace.R
import com.sanjay.myspace.ui.theme.LightBg
import com.sanjay.myspace.ui.theme.PrimaryClr
import com.sanjay.myspace.ui.theme.TextDark

@Composable
fun TextFieldWithLabel(
    label: String,
    modifier: Modifier,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    value: String,
    isEnabled: Boolean = true,
    autoCorrect: Boolean = true,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = TextDark,
        disabledTextColor = TextDark,
        focusedContainerColor = LightBg,
        unfocusedContainerColor = LightBg,
        disabledContainerColor = Color.LightGray,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        errorContainerColor = LightBg,
        focusedLeadingIconColor = PrimaryClr,
        focusedTrailingIconColor = PrimaryClr,
        focusedLabelColor = PrimaryClr,
        cursorColor = PrimaryClr
    ),
    onValueChange: (String) -> Unit,
    errorText: String? = null,
    isError: Boolean = false,
    isOnlyDigits: Boolean = false,
    isDecimal: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    TextField(
        label = { DefaultText(text = label) },
        modifier = modifier
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(
                shape = RoundedCornerShape(10.dp)
            ),
        value = value,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = autoCorrect,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        enabled = isEnabled,
        colors = colors,
        onValueChange = { value ->
            when {
                isDecimal -> {
                    onValueChange(
                        StringBuilder().apply {
                            value.trim().forEach { char ->
                                if (char.isDigit() || (!this.toString()
                                        .contains(".") && char == '.')
                                ) {
                                    append(char)
                                }
                            }
                        }.toString()
                    )
                }

                isOnlyDigits -> {
                    if (value.isDigitsOnly()) {
                        onValueChange(value)
                    }
                }

                else -> {
                    onValueChange(value)
                }
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (value.trim().isNotEmpty()) {
                IconButton(onClick = {
                    onValueChange("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        tint = Color.LightGray,
                        contentDescription = stringResource(R.string.clear)
                    )
                }
            }
        },
        isError = isError,
        supportingText = if (!errorText.isNullOrEmpty()) {
            {
                ErrorText(
                    text = errorText,
                    modifier = Modifier
                )
            }
        } else {
            null
        },
    )
}

@Composable
fun PasswordFieldWithLabel(
    label: String,
    value: String,
    modifier: Modifier,
    imeAction: ImeAction,
    isEnabled: Boolean = true,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = TextDark,
        disabledTextColor = TextDark,
        focusedContainerColor = LightBg,
        unfocusedContainerColor = LightBg,
        disabledContainerColor = Color.LightGray,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        errorContainerColor = LightBg,
        focusedLeadingIconColor = PrimaryClr,
        focusedTrailingIconColor = PrimaryClr,
        focusedLabelColor = PrimaryClr,
        cursorColor = PrimaryClr
    ),
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
) {
    val passwordVisible = remember {
        mutableStateOf(false)
    }

    TextField(
        label = {
            DefaultText(text = label)
        },
        modifier = modifier
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(
                shape = RoundedCornerShape(10.dp)
            ),
        value = value,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        enabled = isEnabled,
        colors = colors,
        onValueChange = {
            onValueChange(it)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            val painterResource = if (passwordVisible.value) {
                painterResource(id = R.drawable.ic_show)
            } else {
                painterResource(id = R.drawable.ic_hide)
            }

            val description = if (passwordVisible.value) {
                stringResource(R.string.hide_password)
            } else {
                stringResource(R.string.show_password)
            }

            IconButton(onClick = {
                passwordVisible.value = !passwordVisible.value
            }) {
                Icon(
                    painter = painterResource,
                    contentDescription = description
                )
            }
        },
        visualTransformation = if (passwordVisible.value)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        isError = isError
    )
}

@Composable
fun ReadOnlyTextField(
    label: String,
    modifier: Modifier,
    value: String,
    colors: TextFieldColors = TextFieldDefaults.colors(
        disabledTextColor = TextDark,
        focusedContainerColor = LightBg,
        unfocusedContainerColor = LightBg,
        disabledContainerColor = Color.LightGray,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        errorContainerColor = LightBg,
    ),
    isError: Boolean = false,
    errorText: String = "",
    textAlign: TextAlign = TextAlign.Start,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    TextField(
        label = { DefaultText(text = label, fontSize = 12.sp) },
        modifier = modifier
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(
                shape = RoundedCornerShape(10.dp)
            ),
        textStyle = TextStyle(
            fontSize = 14.sp,
            textAlign = textAlign
        ),
        value = value,
        enabled = true,
        readOnly = true,
        colors = colors,
        onValueChange = {},
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        minLines = minLines,
        maxLines = maxLines,
        supportingText = if (isError && errorText.isNotEmpty()) {
            {
                ErrorText(
                    text = errorText,
                    modifier = Modifier
                )
            }
        } else {
            null
        }
    )
}