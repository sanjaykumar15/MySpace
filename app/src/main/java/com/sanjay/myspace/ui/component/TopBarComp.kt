package com.sanjay.myspace.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanjay.myspace.R
import com.sanjay.myspace.ui.theme.TextDark
import com.sanjay.myspace.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComp(
    title: String,
    showBackIcon: Boolean = true,
    onBackClick: () -> Unit,
    actions: @Composable () -> Unit? = {},
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        title = {
            Text18(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarColors(
            containerColor = White,
            titleContentColor = TextDark,
            navigationIconContentColor = TextDark,
            actionIconContentColor = TextDark,
            scrolledContainerColor = White
        ),
        navigationIcon = {
            if (showBackIcon) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(enabled = true) {
                            onBackClick()
                        }
                )
            }
        },
        actions = {
            actions()
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TopBarCompPreview() {
    TopBarComp(
        title = "Toolbar",
        onBackClick = {},
        actions = {
            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Excel Download"
                )
            }
        }
    )
}