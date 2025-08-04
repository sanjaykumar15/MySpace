package com.sanjay.myspace.ui.state

import com.sanjay.myspace.model.SelectionItem
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.model.SpaceFolder
import com.sanjay.myspace.utils.ModelUtil
import java.util.ArrayDeque

data class SpaceDetailsState(
    val isInit: Boolean = true,
    val isLoading: Boolean = false,
    val loadingMsg: String? = null,
    val parentIds: ArrayDeque<Int?> = ArrayDeque(),
    val isSpaceParent: Boolean = true,
    val isListView: Boolean = true,
    val showSortByView: Boolean = false,
    val showFolders: Boolean? = null,
    val isFavList: Boolean = false,
    val title: String = "",
    val newFolderName: String = "",
    val newFileName: String = "",
    val fileTitle: String = "",
    val fileDes: String = "",
    val showSelectionView: Boolean = false,
    val showFolderCreateView: Boolean = false,
    val showFileCreateView: Boolean = false,
    val folders: List<SpaceFolder> = emptyList(),
    val files: List<SpaceFile> = emptyList(),
    val sortByList: List<SelectionItem> = ModelUtil.getSortByItems(),
    val showSearchView: Boolean = false,
    val searchQuery: String = "",
    val folderPage: Int = 1,
    val isFolderEndReached: Boolean = false,
    val folderSearchResults: List<SpaceFolder> = emptyList(),
    val filePage: Int = 1,
    val isFileEndReached: Boolean = false,
    val fileSearchResults: List<SpaceFile> = emptyList(),
    val isPageRefreshing: Boolean = false,
) {
    val sortByLabel: String = sortByList.find { it.isSelected }?.value ?: ""
}