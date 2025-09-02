package com.sanjay.myspace.ui.state

import com.sanjay.myspace.model.MySpaceData
import com.sanjay.myspace.model.SelectionItem
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.model.SpaceFolder
import com.sanjay.myspace.utils.ModelUtil

data class MySpaceState(
    val isInit: Boolean = true,
    val isLoading: Boolean = false,
    val loadingMsg: String? = null,
    val isListView: Boolean = true,
    val isFavList: Boolean = false,
    val userId: String = "",
    val newSpaceName: String = "",
    val showCreationView: Boolean = false,
    val showSortByView: Boolean = false,
    val mySpaces: List<MySpaceData> = emptyList(),
    val sortByList: List<SelectionItem> = ModelUtil.getSortByItems(),
    val showSearchView: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<MySpaceData> = emptyList(),
    val endReached: Boolean = false,
    val page: Int = 1,
    val isPageRefreshing: Boolean = false,
    val showLogoutDialog: Boolean = false,

    val selectedParentID: Int? = null,
    val isDetailsListView: Boolean = true,
    val showFolders: Boolean? = null,
    val isDetailsFavList: Boolean = false,
    val folders: List<SpaceFolder> = emptyList(),
    val files: List<SpaceFile> = emptyList(),
    val folderPage: Int = 1,
    val isFolderEndReached: Boolean = false,
    val folderSearchResults: List<SpaceFolder> = emptyList(),
    val filePage: Int = 1,
    val isFileEndReached: Boolean = false,
    val fileSearchResults: List<SpaceFile> = emptyList(),
    val isDetailsPageRefreshing: Boolean = false,
) {
    val sortByLabel: String = sortByList.find { it.isSelected }?.value ?: ""
}