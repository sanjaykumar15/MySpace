package com.sanjay.myspace.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sanjay.myspace.R
import com.sanjay.myspace.ui.theme.PrimaryClr
import com.sanjay.myspace.ui.theme.TertiaryClr
import com.sanjay.myspace.ui.theme.TextDark
import com.sanjay.myspace.ui.theme.White

@Composable
fun ThemeButton(
    text: String,
    modifier: Modifier,
    onClick: () -> Unit,
    containerColor: Color = PrimaryClr,
    textColor: Color = White,
    enable: Boolean = true,
) {
    Button(
        modifier = modifier,
        enabled = enable,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonColors(
            containerColor = containerColor,
            contentColor = textColor,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Black
        ),
        onClick = onClick
    ) {
        Text14(
            text = text,
            modifier = Modifier.padding(7.dp),
            textColor = textColor
        )
    }
}

@Composable
fun ProgressDialog(
    message: String? = null,
    isCancelable: Boolean = false,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = isCancelable,
            dismissOnClickOutside = isCancelable
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(White, shape = RoundedCornerShape(10.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    color = PrimaryClr
                )
                if (!message.isNullOrEmpty()) {
                    Text14(
                        modifier = Modifier.wrapContentSize(),
                        text = message
                    )
                }
            }
        }
    }
}

@Composable
fun ListGridToggle(
    modifier: Modifier,
    isListView: Boolean,
    showFav: Boolean,
    isFavList: Boolean,
    onClick: (Boolean) -> Unit,
    onFavClick: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .wrapContentSize()
            .height(40.dp)
            .background(
                color = Color.Gray
                    .copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        if (showFav) {
            IconButton(
                onClick = {
                    onFavClick(!isFavList)
                }
            ) {
                Icon(
                    imageVector = if (isFavList) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    tint = PrimaryClr,
                    contentDescription = null
                )
            }
        }

        IconButton(
            onClick = {
                onClick(!isListView)
            }
        ) {
            Icon(
                painter = if (isListView) painterResource(R.drawable.ic_lists)
                else painterResource(R.drawable.ic_grid),
                tint = PrimaryClr,
                contentDescription = null
            )
        }
    }
}

@Composable
fun CountView(
    modifier: Modifier,
    count: Int,
) {
    Row(
        modifier = modifier
            .background(
                color = TertiaryClr,
                shape = RoundedCornerShape(topStart = 12.dp)
            )
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp),
            imageVector = Icons.Default.RemoveRedEye,
            contentDescription = null,
            tint = PrimaryClr
        )

        CustomText(
            text = "$count",
            modifier = Modifier,
            fontSize = 10.sp,
            textColor = White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SearchView(
    modifier: Modifier,
    value: String,
    onBackClick: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onBackClick()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.back),
            )
        }

        TextField(
            placeholder = { DefaultText(text = stringResource(R.string.search)) },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            value = value,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors().copy(
                focusedTextColor = TextDark,
                unfocusedTextColor = Color.LightGray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryClr
            ),
            onValueChange = {
                onValueChange(it)
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
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
            }
        )
    }
}