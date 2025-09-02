package com.sanjay.myspace.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanjay.myspace.R
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.model.SpaceFolder
import com.sanjay.myspace.ui.event.SpaceDetailsEvents
import com.sanjay.myspace.ui.screens.SpaceDetailItemView
import com.sanjay.myspace.ui.theme.Gray
import com.sanjay.myspace.ui.theme.LightBg
import com.sanjay.myspace.ui.theme.PrimaryClr

@Composable
fun SpaceDetailsUI(
    modifier: Modifier,
    isInit: Boolean,
    folders: List<SpaceFolder>,
    files: List<SpaceFile>,
    showFolders: Boolean?,
    isListView: Boolean,
    isMobilePortrait: Boolean,
    isFavList: Boolean,
    folderSearchResults: List<SpaceFolder> = emptyList(),
    fileSearchResults: List<SpaceFile> = emptyList(),
    showSearchView: Boolean = false,
    isFolderEndReached: Boolean,
    isFileEndReached: Boolean,
    isPageRefreshing: Boolean,
    onEvent: (SpaceDetailsEvents) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = LightBg
    ) {
        if (!isInit && folders.isEmpty() && files.isEmpty()) {
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

        if (folders.isNotEmpty() || files.isNotEmpty()) {
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
                                onEvent(SpaceDetailsEvents.HandleListType(if (showFolders == true) null else true))
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_folder),
                                tint = if (showFolders == true) PrimaryClr else Gray,
                                contentDescription = stringResource(R.string.folders)
                            )
                        }

                        IconButton(
                            onClick = {
                                onEvent(SpaceDetailsEvents.HandleListType(if (showFolders == false) null else false))
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .rotate(270f),
                                painter = painterResource(R.drawable.ic_file),
                                tint = if (showFolders == false) PrimaryClr else Gray,
                                contentDescription = stringResource(R.string.files)
                            )
                        }
                    }

                    ListGridToggle(
                        modifier = Modifier
                            .wrapContentSize(),
                        isListView = isListView,
                        isFavList = isFavList,
                        showFav = showFolders != true,
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
                            isListView -> 1
                            !isListView && isMobilePortrait -> 2
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
                    if (!isFavList && showFolders != false) {
                        itemsIndexed(if (showSearchView) folderSearchResults else folders) { index, folderItem ->
                            if (index >= folders.size - 1 && !showSearchView && !isFolderEndReached && !isPageRefreshing) {
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

                    if (isFavList || showFolders != true) {
                        itemsIndexed(if (showSearchView) fileSearchResults else files) { index, fileItem ->
                            if (index >= files.size - 1 && !showSearchView && !isFileEndReached && !isPageRefreshing) {
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

                    if (isPageRefreshing) {
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

                    if (isFolderEndReached && isFileEndReached) {
                        item {
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }
            }
        }
    }
}