package com.sanjay.myspace.ui.event

sealed class SpaceDetailsEvents {
    data object OnBackPressed : SpaceDetailsEvents()
    data object OnDeleteClicked : SpaceDetailsEvents()
    data object OnRefresh : SpaceDetailsEvents()
    data object ClearSelection : SpaceDetailsEvents()
    data object CallPaginationAPI : SpaceDetailsEvents()

    data class UpdateDSData(
        val parentId: Int?,
    ) : SpaceDetailsEvents()

    data class OnInit(
        val parentId: Int?,
        val isUpdate: Boolean = false,
        val isBack: Boolean = false,
    ) : SpaceDetailsEvents()

    data class HandleSearchVisibility(
        val show: Boolean,
    ) : SpaceDetailsEvents()

    data class OnSearchQuery(
        val query: String,
    ) : SpaceDetailsEvents()

    data class HandleSelectionView(
        val show: Boolean,
    ) : SpaceDetailsEvents()

    data class HandleFolderCreationView(
        val show: Boolean,
    ) : SpaceDetailsEvents()

    data class HandleFileCreationView(
        val show: Boolean,
    ) : SpaceDetailsEvents()

    data class OnFolderNameChanged(
        val name: String,
    ) : SpaceDetailsEvents()

    data class OnFileNameChanged(
        val name: String,
    ) : SpaceDetailsEvents()

    data class OnFileTitleChanged(
        val title: String,
    ) : SpaceDetailsEvents()

    data class OnFileDesChanged(
        val description: String,
    ) : SpaceDetailsEvents()

    data class OnItemClicked(
        val folderId: Int? = null,
        val fileId: Int? = null,
        val isLongClicked: Boolean = false,
    ) : SpaceDetailsEvents()

    data class OnCreateClicked(
        val isFolderCreation: Boolean,
    ) : SpaceDetailsEvents()

    data class OnViewToggleClicked(
        val isListView: Boolean,
    ) : SpaceDetailsEvents()

    data class HandleListType(
        val showFolders: Boolean?,
    ) : SpaceDetailsEvents()

    data class HandleFav(
        val isFavList: Boolean,
    ) : SpaceDetailsEvents()

    data class HandleSortByView(
        val show: Boolean,
    ) : SpaceDetailsEvents()

    data class OnSortByClicked(
        val sortBy: String,
    ) : SpaceDetailsEvents()

    data class OnFavoriteClicked(
        val id: Int?,
    ) : SpaceDetailsEvents()
}
