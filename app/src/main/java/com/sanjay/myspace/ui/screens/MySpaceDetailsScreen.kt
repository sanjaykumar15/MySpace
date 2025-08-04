package com.sanjay.myspace.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanjay.myspace.R
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.model.SpaceFolder
import com.sanjay.myspace.ui.component.CountView
import com.sanjay.myspace.ui.component.CustomText
import com.sanjay.myspace.ui.component.FileCreationView
import com.sanjay.myspace.ui.component.FolderCreationView
import com.sanjay.myspace.ui.component.ListGridToggle
import com.sanjay.myspace.ui.component.ProgressDialog
import com.sanjay.myspace.ui.component.SearchView
import com.sanjay.myspace.ui.component.SelectionWOClear
import com.sanjay.myspace.ui.component.Text14
import com.sanjay.myspace.ui.component.TopBarComp
import com.sanjay.myspace.ui.event.SpaceDetailsEvents
import com.sanjay.myspace.ui.state.SpaceDetailsState
import com.sanjay.myspace.ui.theme.Gray
import com.sanjay.myspace.ui.theme.Green
import com.sanjay.myspace.ui.theme.LightBg
import com.sanjay.myspace.ui.theme.PrimaryClr
import com.sanjay.myspace.ui.theme.SecondaryClr
import com.sanjay.myspace.ui.theme.TextDark
import com.sanjay.myspace.ui.theme.White
import com.sanjay.myspace.utils.DeviceConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySpaceDetailsScreen(
    parentId: Int?,
    state: SpaceDetailsState,
    onEvent: (SpaceDetailsEvents) -> Unit,
) {
    if (state.isInit || state.isLoading) {
        if (state.isInit) onEvent(SpaceDetailsEvents.UpdateDSData(parentId))
        ProgressDialog(state.loadingMsg ?: "Fetching data...")
    }

    if (state.showSelectionView) {
        CreationSelection(
            onEvent = onEvent
        )
    }

    if (state.showFolderCreateView) {
        ModalBottomSheet(
            containerColor = White,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = {
                onEvent(SpaceDetailsEvents.HandleFolderCreationView(false))
            }
        ) {
            FolderCreationView(
                modifier = Modifier
                    .fillMaxWidth(),
                folderName = state.newFolderName,
                onFolderNameChanged = {
                    onEvent(SpaceDetailsEvents.OnFolderNameChanged(it))
                },
                onCreate = {
                    onEvent(SpaceDetailsEvents.OnCreateClicked(true))
                }
            )
        }
    }

    if (state.showFileCreateView) {
        ModalBottomSheet(
            containerColor = White,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = {
                onEvent(SpaceDetailsEvents.HandleFileCreationView(false))
            }
        ) {
            FileCreationView(
                modifier = Modifier
                    .fillMaxWidth(),
                fileName = state.newFileName,
                fileTitle = state.fileTitle,
                fileDes = state.fileDes,
                onFileNameChanged = {
                    onEvent(SpaceDetailsEvents.OnFileNameChanged(it))
                },
                onFileTitleChanged = {
                    onEvent(SpaceDetailsEvents.OnFileTitleChanged(it))
                },
                onFileDesChanged = {
                    onEvent(SpaceDetailsEvents.OnFileDesChanged(it))
                },
                onCreate = {
                    onEvent(SpaceDetailsEvents.OnCreateClicked(false))
                }
            )
        }
    }

    if (state.showSortByView) {
        SelectionWOClear(
            dialogLabel = "Sort By",
            items = state.sortByList,
            isDeselect = false,
            onDismiss = {
                onEvent(SpaceDetailsEvents.HandleSortByView(false))
            },
            onClick = {
                onEvent(SpaceDetailsEvents.OnSortByClicked(it?.value ?: ""))
            }
        )
    }

    BackHandler {
        backPress(
            state = state,
            onEvent = onEvent
        )
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
                        onEvent(SpaceDetailsEvents.HandleSearchVisibility(false))
                    },
                    onValueChange = {
                        onEvent(SpaceDetailsEvents.OnSearchQuery(it))
                    },
                    value = state.searchQuery
                )
            } else {
                TopBarComp(
                    title = state.title,
                    onBackClick = {
                        backPress(
                            state = state,
                            onEvent = onEvent
                        )
                    },
                    actions = {
                        if (state.folders.any { it.isSelected } || state.files.any { it.isSelected }) {
                            TextButton(
                                onClick = {
                                    onEvent(SpaceDetailsEvents.OnDeleteClicked)
                                }
                            ) {
                                Text14(
                                    text = "Delete",
                                    modifier = Modifier
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                onEvent(SpaceDetailsEvents.HandleSortByView(true))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                onEvent(SpaceDetailsEvents.HandleSearchVisibility(true))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search),
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (state.showFolders) {
                        null -> onEvent(SpaceDetailsEvents.HandleSelectionView(true))
                        true -> onEvent(SpaceDetailsEvents.HandleFolderCreationView(true))
                        false -> onEvent(SpaceDetailsEvents.HandleFileCreationView(true))
                    }
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
            if (!state.isInit && state.folders.isEmpty() && state.files.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CustomText(
                        text = "No file or folder has found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                onEvent(SpaceDetailsEvents.HandleListType(null))
                return@Surface
            }

            if (state.folders.isNotEmpty() || state.files.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
                        ) {
                            IconButton(
                                onClick = {
                                    onEvent(SpaceDetailsEvents.HandleListType(if (state.showFolders == true) null else true))
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_folder),
                                    tint = if (state.showFolders == true) PrimaryClr else Gray,
                                    contentDescription = stringResource(R.string.folders)
                                )
                            }

                            IconButton(
                                onClick = {
                                    onEvent(SpaceDetailsEvents.HandleListType(if (state.showFolders == false) null else false))
                                }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .rotate(270f),
                                    painter = painterResource(R.drawable.ic_file),
                                    tint = if (state.showFolders == false) PrimaryClr else Gray,
                                    contentDescription = stringResource(R.string.files)
                                )
                            }
                        }

                        ListGridToggle(
                            modifier = Modifier
                                .wrapContentSize(),
                            isListView = state.isListView,
                            isFavList = state.isFavList,
                            showFav = state.showFolders != true,
                            onFavClick = {
                                onEvent(SpaceDetailsEvents.HandleFav(it))
                            },
                            onClick = {
                                onEvent(SpaceDetailsEvents.OnViewToggleClicked(it))
                            }
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(
                            when {
                                state.isListView -> 1
                                !state.isListView && isMobilePortrait -> 2
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
                        if (!state.isFavList && state.showFolders != false) {
                            itemsIndexed(if (state.showSearchView) state.folderSearchResults else state.folders) { index, folderItem ->
                                if (index >= state.folders.size - 1 && !state.showSearchView && !state.isFolderEndReached && !state.isPageRefreshing) {
                                    onEvent(SpaceDetailsEvents.CallPaginationAPI)
                                }
                                SpaceDetailItemView(
                                    folderItem = folderItem,
                                    fileItem = null,
                                    isFolder = true,
                                    onItemClicked = {
                                        onEvent(SpaceDetailsEvents.OnItemClicked(folderId = it))
                                    },
                                    onItemLongClicked = {
                                        onEvent(
                                            SpaceDetailsEvents.OnItemClicked(
                                                folderId = it,
                                                isLongClicked = true
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        if (state.isFavList || state.showFolders != true) {
                            itemsIndexed(if (state.showSearchView) state.fileSearchResults else state.files) { index, fileItem ->
                                if (index >= state.files.size - 1 && !state.showSearchView && !state.isFileEndReached && !state.isPageRefreshing) {
                                    onEvent(SpaceDetailsEvents.CallPaginationAPI)
                                }
                                SpaceDetailItemView(
                                    folderItem = null,
                                    fileItem = fileItem,
                                    isFolder = false,
                                    onItemClicked = {
                                        onEvent(SpaceDetailsEvents.OnItemClicked(fileId = it))
                                    },
                                    onItemLongClicked = {
                                        onEvent(
                                            SpaceDetailsEvents.OnItemClicked(
                                                fileId = it,
                                                isLongClicked = true
                                            )
                                        )
                                    },
                                    onFavClicked = {
                                        onEvent(
                                            SpaceDetailsEvents.OnFavoriteClicked(it)
                                        )
                                    }
                                )
                            }
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

                        if (state.isFolderEndReached && state.isFileEndReached) {
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
private fun SpaceDetailItemView(
    folderItem: SpaceFolder?,
    fileItem: SpaceFile?,
    isFolder: Boolean,
    onItemClicked: (Int) -> Unit,
    onItemLongClicked: (Int) -> Unit,
    onFavClicked: (Int?) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = (isFolder && folderItem?.id != null) || (!isFolder && fileItem?.id != null),
                onClick = {
                    onItemClicked(
                        if (isFolder) folderItem!!.id!!
                        else fileItem!!.id!!
                    )
                },
                onLongClick = {
                    onItemLongClicked(
                        if (isFolder) folderItem!!.id!!
                        else fileItem!!.id!!
                    )
                }
            ),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(
            containerColor = White,
            contentColor = TextDark
        )
    ) {
        Box {
            if (folderItem?.isSelected == true || fileItem?.isSelected == true) {
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
                            .heightIn(min = 48.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (isFolder) 0f else 270f),
                                painter = if (isFolder) painterResource(R.drawable.ic_folder)
                                else painterResource(R.drawable.ic_file),
                                contentDescription = if (isFolder) stringResource(R.string.folder)
                                else stringResource(R.string.file),
                                colorFilter = ColorFilter.tint(color = SecondaryClr)
                            )

                            Text14(
                                text = if (isFolder) folderItem!!.name
                                else fileItem!!.title,
                                modifier = Modifier
                                    .weight(1f)
                            )

                            if (!isFolder && fileItem != null) {
                                Icon(
                                    modifier = Modifier
                                        .clickable {
                                            onFavClicked(fileItem.id)
                                        },
                                    imageVector = if (fileItem.isFavorite) Icons.Default.Favorite
                                    else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (fileItem.isFavorite) PrimaryClr else TextDark
                                )
                            }
                        }

                        CustomText(
                            text = if (!isFolder) {
                                fileItem!!.description
                            } else folderItem!!.updatedAt,
                            modifier = Modifier,
                            fontSize = 12.sp,
                            minLines = 2,
                            maxLines = 2,
                            textOverflow = TextOverflow.Ellipsis
                        )
                    }

                    if (isFolder) {
                        Icon(
                            modifier = Modifier
                                .size(28.dp)
                                .padding(end = 10.dp),
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                        )
                    }
                }

                CountView(
                    modifier = Modifier
                        .align(Alignment.End),
                    count = (if (isFolder) folderItem?.viewCount
                    else fileItem?.viewCount) ?: 0
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreationSelection(
    onEvent: (SpaceDetailsEvents) -> Unit,
) {
    ModalBottomSheet(
        containerColor = White,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        onDismissRequest = {
            onEvent(SpaceDetailsEvents.HandleSelectionView(false))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.5.dp, PrimaryClr),
                colors = CardDefaults.cardColors().copy(
                    containerColor = White,
                    contentColor = TextDark,
                ),
                onClick = {
                    onEvent(SpaceDetailsEvents.HandleFolderCreationView(true))
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_folder),
                        contentDescription = stringResource(R.string.new_folder)
                    )

                    Text14(
                        text = stringResource(R.string.new_folder),
                        modifier = Modifier
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.5.dp, PrimaryClr),
                colors = CardDefaults.cardColors().copy(
                    containerColor = White,
                    contentColor = TextDark,
                ),
                onClick = {
                    onEvent(SpaceDetailsEvents.HandleFileCreationView(true))
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .rotate(270f),
                        painter = painterResource(R.drawable.ic_file),
                        contentDescription = stringResource(R.string.new_file)
                    )

                    Text14(
                        text = stringResource(R.string.new_file),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

private fun backPress(
    state: SpaceDetailsState,
    onEvent: (SpaceDetailsEvents) -> Unit,
) {
    if (state.showSearchView) {
        onEvent(SpaceDetailsEvents.HandleSearchVisibility(false))
    } else if (state.parentIds.size > 1) {
        onEvent(
            SpaceDetailsEvents.OnInit(
                parentId = state.parentIds.pop(),
                isBack = true
            )
        )
    } else if (state.folders.any { it.isSelected } ||
        state.files.any { it.isSelected }) {
        onEvent(SpaceDetailsEvents.ClearSelection)
    } else {
        onEvent(SpaceDetailsEvents.OnBackPressed)
    }
}