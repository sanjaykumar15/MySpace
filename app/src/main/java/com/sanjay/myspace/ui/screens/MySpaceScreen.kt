package com.sanjay.myspace.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanjay.myspace.R
import com.sanjay.myspace.model.MySpaceData
import com.sanjay.myspace.ui.component.AlertDialog
import com.sanjay.myspace.ui.component.CountView
import com.sanjay.myspace.ui.component.CustomText
import com.sanjay.myspace.ui.component.ListGridToggle
import com.sanjay.myspace.ui.component.ProgressDialog
import com.sanjay.myspace.ui.component.SearchView
import com.sanjay.myspace.ui.component.SelectionWOClear
import com.sanjay.myspace.ui.component.Text12
import com.sanjay.myspace.ui.component.Text14
import com.sanjay.myspace.ui.component.TextFieldWithLabel
import com.sanjay.myspace.ui.component.ThemeButton
import com.sanjay.myspace.ui.component.TopBarComp
import com.sanjay.myspace.ui.event.MySpaceEvents
import com.sanjay.myspace.ui.state.MySpaceState
import com.sanjay.myspace.ui.theme.Green
import com.sanjay.myspace.ui.theme.LightBg
import com.sanjay.myspace.ui.theme.MySpaceTheme
import com.sanjay.myspace.ui.theme.PrimaryClr
import com.sanjay.myspace.ui.theme.TextDark
import com.sanjay.myspace.ui.theme.White
import com.sanjay.myspace.utils.DeviceConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySpaceScreen(
    state: MySpaceState,
    onEvent: (MySpaceEvents) -> Unit,
) {
    if (state.isInit || state.isLoading) {
        ProgressDialog(state.loadingMsg ?: "Fetching data...")
    }

    if (state.showLogoutDialog) {
        AlertDialog(
            message = stringResource(R.string.logout_msg),
            positiveButtonText = stringResource(R.string.yes),
            negativeButtonText = stringResource(R.string.cancel),
            onDismiss = {
                onEvent(MySpaceEvents.HandleLogoutDialog(false))
            },
            onConfirm = {
                onEvent(MySpaceEvents.OnLogoutClicked)
            }
        )
    }

    if (state.showCreationView) {
        ModalBottomSheet(
            containerColor = White,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = {
                onEvent(MySpaceEvents.HandleCreationView(false))
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextFieldWithLabel(
                    label = stringResource(R.string.spaceName),
                    value = state.newSpaceName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    onValueChange = {
                        onEvent(MySpaceEvents.OnNameChanged(it))
                    },
                )

                ThemeButton(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    text = stringResource(R.string.create),
                    onClick = {
                        onEvent(MySpaceEvents.OnCreateClicked)
                    }
                )
            }
        }
    }

    if (state.showSortByView) {
        SelectionWOClear(
            dialogLabel = "Sort By",
            items = state.sortByList,
            isDeselect = false,
            onDismiss = {
                onEvent(MySpaceEvents.HandleSortByView(false))
            },
            onClick = {
                onEvent(MySpaceEvents.OnSortByClicked(it?.value ?: ""))
            }
        )
    }

    BackHandler {
        if (state.showSearchView) {
            onEvent(MySpaceEvents.HandleSearchVisibility(false))
        } else if (state.mySpaces.any { it.isSelected }) {
            onEvent(MySpaceEvents.ClearSelection)
        } else {
            onEvent(MySpaceEvents.OnBackClicked)
        }
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val isMobilePortrait = remember(deviceConfiguration) {
        deviceConfiguration == DeviceConfiguration.MOBILE_PORTRAIT
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = White,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (state.showSearchView) {
                SearchView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = White)
                        .padding(
                            top = WindowInsets.statusBars
                                .asPaddingValues()
                                .calculateTopPadding()
                        ),
                    onBackClick = {
                        onEvent(MySpaceEvents.HandleSearchVisibility(false))
                    },
                    onValueChange = {
                        onEvent(MySpaceEvents.OnSearchQuery(it))
                    },
                    value = state.searchQuery
                )
            } else {
                TopBarComp(
                    title = stringResource(R.string.mySpace),
                    showBackIcon = false,
                    onBackClick = {},
                    actions = {
                        if (state.mySpaces.any { it.isSelected }) {
                            TextButton(
                                onClick = {
                                    onEvent(MySpaceEvents.OnDeleteClicked)
                                }
                            ) {
                                Text14(
                                    text = "Delete",
                                    modifier = Modifier
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable(enabled = true) {
                                    onEvent(MySpaceEvents.HandleSearchVisibility(true))
                                }
                        )

                        IconButton(
                            onClick = {
                                onEvent(MySpaceEvents.HandleLogoutDialog(true))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(R.string.logout),
                                tint = PrimaryClr
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(MySpaceEvents.HandleCreationView(true))
                },
                containerColor = PrimaryClr,
                contentColor = White,
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = LightBg
        ) {
            if (!state.isInit && state.mySpaces.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CustomText(
                        text = "No Space has created",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                return@Surface
            }

            if (state.mySpaces.isNotEmpty() || state.searchResults.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!state.showSearchView) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp, vertical = 10.dp)
                                    .wrapContentSize()
                                    .height(40.dp)
                                    .background(
                                        color = Color.Gray
                                            .copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onEvent(MySpaceEvents.HandleSortByView(true))
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(start = 10.dp),
                                    imageVector = Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = null
                                )

                                Text12(
                                    text = state.sortByLabel,
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                )
                            }
                        } else {
                            Box { }
                        }

                        ListGridToggle(
                            modifier = Modifier
                                .wrapContentSize(),
                            isListView = state.isListView,
                            showFav = true,
                            isFavList = state.isFavList,
                            onFavClick = {
                                onEvent(MySpaceEvents.HandleFav(it))
                            },
                            onClick = {
                                onEvent(MySpaceEvents.OnViewToggleClicked(it))
                            }
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(
                            when {
                                state.isListView -> 1
                                isMobilePortrait -> 2
                                else -> 3
                            }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(if (state.showSearchView) state.searchResults else state.mySpaces) { index, spaceItem ->
                            if (index >= state.mySpaces.size - 1 && !state.showSearchView && !state.endReached && !state.isPageRefreshing) {
                                onEvent(MySpaceEvents.CallPaginationAPI)
                            }
                            MySpaceItemView(
                                item = spaceItem,
                                onItemClicked = {
                                    onEvent(MySpaceEvents.OnItemClicked(it))
                                },
                                onItemLongClicked = {
                                    onEvent(
                                        MySpaceEvents.OnItemClicked(
                                            id = it,
                                            isLongClicked = true
                                        )
                                    )
                                },
                                onFavClicked = {
                                    onEvent(MySpaceEvents.OnFavClicked(it))
                                }
                            )
                        }

                        if (state.isPageRefreshing) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (state.endReached) {
                            item {
                                Spacer(modifier = Modifier.height(70.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MySpaceItemView(
    item: MySpaceData,
    onItemClicked: (Int) -> Unit,
    onItemLongClicked: (Int) -> Unit,
    onFavClicked: (Int?) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = item.id != null,
                onClick = {
                    onItemClicked(item.id!!)
                },
                onLongClick = {
                    onItemLongClicked(item.id!!)
                }
            ),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(
            containerColor = White,
            contentColor = TextDark
        )
    ) {
        Box {
            if (item.isSelected) {
                Icon(
                    modifier = Modifier
                        .height(20.dp)
                        .width(24.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = Green.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(bottomEnd = 12.dp)
                        ),
                    imageVector = Icons.Default.Check,
                    tint = White,
                    contentDescription = "Selected",
                )
            }

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text14(
                                text = item.name,
                                modifier = Modifier
                                    .weight(1f)
                            )

                            Icon(
                                modifier = Modifier
                                    .clickable {
                                        onFavClicked(item.id)
                                    },
                                imageVector = if (item.isFavorite) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (item.isFavorite) PrimaryClr else TextDark
                            )
                        }

                        Text12(
                            text = item.updatedAt,
                            modifier = Modifier
                        )
                    }
                }

                CountView(
                    modifier = Modifier
                        .align(Alignment.End),
                    count = item.viewCount
                )
            }
        }
    }
}

@Preview
@Composable
private fun MySpaceScreenPreview() {
    MySpaceTheme {
        MySpaceScreen(
            state = MySpaceState(),
            onEvent = {}
        )
    }
}