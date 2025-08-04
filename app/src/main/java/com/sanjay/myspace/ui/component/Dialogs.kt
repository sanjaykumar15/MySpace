package com.sanjay.myspace.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sanjay.myspace.R
import com.sanjay.myspace.model.SelectionItem
import com.sanjay.myspace.ui.theme.LightGrey
import com.sanjay.myspace.ui.theme.PrimaryClr
import com.sanjay.myspace.ui.theme.SecondaryClr
import com.sanjay.myspace.ui.theme.TextDark
import com.sanjay.myspace.ui.theme.White

private val dialogCornerShape: Shape = RoundedCornerShape(10.dp)

@Composable
fun SelectionWOClear(
    dialogLabel: String,
    items: List<SelectionItem>,
    isDeselect: Boolean = true,
    onDismiss: () -> Unit,
    onClick: (SelectionItem?) -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        val lazyListState = rememberLazyListState(
            initialFirstVisibleItemIndex = if (items.any { it.isSelected })
                items.indexOf(items.find { it.isSelected })
            else 0
        )
        Box(
            modifier = Modifier
                .background(
                    color = White,
                    shape = dialogCornerShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomText(
                        text = "Select $dialogLabel",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        fontSize = 16.sp
                    )

                    IconButton(
                        modifier = Modifier.size(22.dp),
                        colors = IconButtonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.White
                        ),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                if (items.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Text14(
                            text = "No Data found",
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                    ) {
                        itemsIndexed(items) { idx, item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(
                                        color = if (item.isSelected) {
                                            SecondaryClr
                                        } else {
                                            White
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable(enabled = true) {
                                        if (isDeselect) {
                                            if (!item.isSelected) {
                                                onClick(item)
                                            } else {
                                                onClick(null)
                                            }
                                        } else {
                                            onClick(item)
                                        }
                                    }
                            ) {
                                Text12(
                                    text = item.label,
                                    textColor = if (item.isSelected) White else TextDark,
                                    fontWeight = if (item.isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                )
                            }
                            if (idx != items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        horizontal = 15.dp,
                                        vertical = 2.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlertDialog(
    message: String,
    isCancelable: Boolean = true,
    positiveButtonText: String = stringResource(R.string.ok),
    negativeButtonText: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = isCancelable,
            dismissOnClickOutside = isCancelable
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = White, shape = dialogCornerShape)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text14(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly
                ) {
                    if (!negativeButtonText.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = LightGrey.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text12(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(vertical = 7.dp, horizontal = 20.dp),
                                text = negativeButtonText,
                                textColor = Color.DarkGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = PrimaryClr,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onConfirm()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text12(
                            modifier = Modifier
                                .padding(vertical = 7.dp, horizontal = 20.dp),
                            text = positiveButtonText,
                            textColor = White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}