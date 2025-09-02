package com.sanjay.myspace.ui.event

sealed class MySpaceEvents {

    data object OnBackClicked : MySpaceEvents()
    data object OnRefresh : MySpaceEvents()
    data object ClearSelection : MySpaceEvents()
    data object CallPaginationAPI : MySpaceEvents()
    data object OnCreateClicked : MySpaceEvents()
    data object OnDeleteClicked : MySpaceEvents()
    data object OnLogoutClicked : MySpaceEvents()
    data object DetailsBack : MySpaceEvents()

    data class HandleSearchVisibility(
        val show: Boolean,
    ) : MySpaceEvents()

    data class OnSearchQuery(
        val query: String,
    ) : MySpaceEvents()

    data class OnViewToggleClicked(
        val isListView: Boolean,
    ) : MySpaceEvents()

    data class OnItemClicked(
        val id: Int,
        val isLongClicked: Boolean = false,
    ) : MySpaceEvents()

    data class HandleCreationView(
        val show: Boolean,
    ) : MySpaceEvents()

    data class HandleSortByView(
        val show: Boolean,
    ) : MySpaceEvents()

    data class OnSortByClicked(
        val sortBy: String,
    ) : MySpaceEvents()

    data class OnNameChanged(
        val name: String,
    ) : MySpaceEvents()

    data class HandleLogoutDialog(
        val show: Boolean,
    ) : MySpaceEvents()

    data class OnFavClicked(
        val id: Int?,
    ) : MySpaceEvents()

    data class HandleFav(
        val isFavList: Boolean,
    ) : MySpaceEvents()

}