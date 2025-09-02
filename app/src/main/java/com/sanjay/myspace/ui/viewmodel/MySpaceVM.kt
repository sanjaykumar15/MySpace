package com.sanjay.myspace.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjay.myspace.helper.DataStoreHelper
import com.sanjay.myspace.helper.DataStoreKey
import com.sanjay.myspace.helper.FirebaseHelper
import com.sanjay.myspace.helper.RealmHelper
import com.sanjay.myspace.model.SortEnum
import com.sanjay.myspace.paginator.PaginatorImpl
import com.sanjay.myspace.ui.event.MySpaceEvents
import com.sanjay.myspace.ui.event.SpaceDetailsEvents
import com.sanjay.myspace.ui.state.MySpaceState
import com.sanjay.myspace.utils.ModelUtil.sortSpaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MySpaceVM @Inject constructor(
    private val realmHelper: RealmHelper,
    private val firebaseHelper: FirebaseHelper,
    private val dataStoreHelper: DataStoreHelper,
) : ViewModel() {

    init {
        setUserId()
    }

    var state by mutableStateOf(MySpaceState())
        private set

    private val pageLimit: Int = 10
    private val pagination = PaginatorImpl(
        initialKey = 2,
        onLoadUpdated = {
            state = state.copy(
                isPageRefreshing = it
            )
        },
        getNextKey = {
            state.page + 1
        },
        onRequest = { nextPage ->
            realmHelper.getMySpaces(
                userId = state.userId,
                sortBy = state.sortByLabel,
                page = state.page,
                limit = pageLimit,
                isFavList = state.isFavList
            )
        },
        onSuccess = { items, newKey ->
            state = state.copy(
                mySpaces = state.mySpaces + items.filter { filterRes ->
                    !state.mySpaces.map { it.id }.contains(filterRes.id)
                },
                page = newKey,
                endReached = items.size < pageLimit
            )
        }
    )

    private fun setUserId() {
        viewModelScope.launch(Dispatchers.Main) {
            val userId = dataStoreHelper.getString(DataStoreKey.USER_ID)
            val sortBy = dataStoreHelper.getString(
                DataStoreKey.SPACE_SORT_PREF,
                SortEnum.UPDATED_AT_DES.name
            )
            val results = realmHelper.getMySpaces(
                userId = userId,
                sortBy = sortBy,
                page = state.page,
                limit = pageLimit
            )
            state = state.copy(
                userId = userId,
                isInit = false,
                isLoading = false,
                isListView = dataStoreHelper.getBoolean(DataStoreKey.SPACE_LIST_PREF, true),
                mySpaces = results,
                page = state.page + 1,
                endReached = results.size < pageLimit,
                sortByList = state.sortByList.map {
                    it.copy(isSelected = it.value == sortBy)
                }
            )
        }
    }

    fun eventHandler(event: MySpaceEvents) {
        when (event) {
            MySpaceEvents.OnRefresh -> {
                getSpaces()
            }

            MySpaceEvents.DetailsBack -> {
                state = state.copy(
                    selectedParentID = null
                )
            }

            is MySpaceEvents.OnViewToggleClicked -> {
                state = state.copy(
                    isListView = event.isListView
                )
                updateListViewPref(event.isListView)
            }

            is MySpaceEvents.HandleCreationView -> {
                state = state.copy(
                    showCreationView = event.show,
                    newSpaceName = ""
                )
            }

            is MySpaceEvents.HandleSortByView -> {
                state = state.copy(
                    showSortByView = event.show
                )
            }

            is MySpaceEvents.OnSortByClicked -> {
                state = state.copy(
                    showSortByView = false,
                    sortByList = state.sortByList.map {
                        it.copy(isSelected = it.value == event.sortBy)
                    }
                )
                if (state.endReached) {
                    state = state.copy(
                        mySpaces = state.mySpaces.sortSpaces(state.sortByLabel)
                    )
                } else {
                    getSpaces()
                }
                updateSortByPref(event.sortBy)
            }

            is MySpaceEvents.OnNameChanged -> {
                state = state.copy(
                    newSpaceName = event.name
                )
            }

            is MySpaceEvents.OnItemClicked -> {
                if (event.isLongClicked) {
                    state = state.copy(
                        mySpaces = state.mySpaces.map {
                            if (it.id == event.id) {
                                it.copy(isSelected = !it.isSelected)
                            } else {
                                it
                            }
                        }
                    )
                }
                if (!event.isPortrait) {
                    updateSpaceDetailsData(event.id)
                }
            }

            MySpaceEvents.ClearSelection -> {
                state = state.copy(
                    mySpaces = state.mySpaces.map {
                        it.copy(isSelected = false)
                    }
                )
            }

            MySpaceEvents.OnCreateClicked -> {
                createSpace()
            }

            MySpaceEvents.OnDeleteClicked -> {
                deleteSpace()
            }

            is MySpaceEvents.HandleSearchVisibility -> {
                state = state.copy(
                    showSearchView = event.show,
                    searchQuery = "",
                    searchResults = if (event.show) state.mySpaces else emptyList()
                )
            }

            is MySpaceEvents.OnSearchQuery -> {
                state = state.copy(
                    searchQuery = event.query
                )
                if (event.query.isNotEmpty()) {
                    val searchFilteredRes = state.mySpaces.filter {
                        it.name.contains(event.query, true)
                    }
                    if (searchFilteredRes.isNotEmpty() ||
                        state.endReached
                    ) {
                        state = state.copy(
                            searchResults = searchFilteredRes
                        )
                    } else {
                        search(event.query)
                    }
                } else {
                    state = state.copy(
                        searchResults = if (state.showSearchView) state.mySpaces else emptyList()
                    )
                }
            }

            MySpaceEvents.CallPaginationAPI -> {
                viewModelScope.launch(Dispatchers.Main) {
                    pagination.loadNextItems()
                }
            }

            is MySpaceEvents.HandleLogoutDialog -> {
                state = state.copy(
                    showLogoutDialog = event.show
                )
            }

            is MySpaceEvents.OnFavClicked -> {
                realmHelper.updateFav(id = event.id, isFile = false)
                state = state.copy(
                    mySpaces = state.mySpaces.map {
                        if (it.id == event.id) {
                            it.copy(isFavorite = !it.isFavorite)
                        } else {
                            it
                        }
                    }
                )
            }

            is MySpaceEvents.HandleFav -> {
                state = state.copy(
                    isFavList = event.isFavList
                )
                getSpaces()
            }

            else -> {}
        }
    }

    fun onDetailedEvent(event: SpaceDetailsEvents) {
        when (event) {
            is SpaceDetailsEvents.OnItemClicked -> {
                if (event.isLongClicked) {
                    state = state.copy(
                        folders = state.folders.map {
                            if (it.id == event.folderId) {
                                it.copy(isSelected = !it.isSelected)
                            } else {
                                it
                            }
                        },
                        files = state.files.map {
                            if (it.id == event.fileId) {
                                it.copy(isSelected = !it.isSelected)
                            } else {
                                it
                            }
                        },
                    )
                }
            }

            SpaceDetailsEvents.ClearSelection -> {
                state = state.copy(
                    folders = state.folders.map {
                        it.copy(isSelected = false)
                    },
                    files = state.files.map {
                        it.copy(isSelected = false)
                    },
                )
            }

            is SpaceDetailsEvents.OnViewToggleClicked -> {
                state = state.copy(
                    isListView = event.isListView
                )
                updateListViewPref(event.isListView)
            }

            is SpaceDetailsEvents.HandleListType -> {
                state = state.copy(
                    showFolders = event.showFolders,
                    isFavList = if (event.showFolders == true) false
                    else state.isFavList
                )
                refreshFileData()
            }

            is SpaceDetailsEvents.HandleSearchVisibility -> {
                state = state.copy(
                    showSearchView = event.show,
                    searchQuery = "",
                    folderSearchResults = if (event.show) state.folders else emptyList(),
                    fileSearchResults = if (event.show) state.files else emptyList()
                )
            }

            is SpaceDetailsEvents.OnSearchQuery -> {
                state = state.copy(
                    searchQuery = event.query
                )
                if (event.query.isNotEmpty()) {
                    val folderSearchRes = state.folders.filter {
                        it.name.contains(event.query, true)
                    }
                    val fileSearchRes = state.files.filter {
                        it.title.contains(event.query, true)
                    }
                    if (state.isFileEndReached) {
                        state = state.copy(
                            folderSearchResults = folderSearchRes,
                            fileSearchResults = fileSearchRes
                        )
                    } else if (folderSearchRes.isNotEmpty() || fileSearchRes.isNotEmpty()) {
                        state = state.copy(
                            folderSearchResults = folderSearchRes,
                            fileSearchResults = fileSearchRes
                        )
                    } else {
                        search(event.query)
                    }
                } else {
                    state = state.copy(
                        folderSearchResults = if (state.showSearchView) state.folders else emptyList(),
                        fileSearchResults = if (state.showSearchView) state.files else emptyList()
                    )
                }
            }

            SpaceDetailsEvents.CallPaginationAPI -> {
                viewModelScope.launch(Dispatchers.Main) {
                    pagination.loadNextItems()
                }
            }

            is SpaceDetailsEvents.OnFavoriteClicked -> {
                realmHelper.updateFav(
                    id = event.id,
                    isFile = true
                )
                state = state.copy(
                    files = state.files.map {
                        if (it.id == event.id) {
                            it.copy(isFavorite = !it.isFavorite)
                        } else {
                            it
                        }
                    }
                )
            }

            is SpaceDetailsEvents.HandleFav -> {
                state = state.copy(
                    isFavList = event.isFavList
                )
                refreshFileData()
            }

            else -> {}
        }
    }

    fun spaceNameValidation(): String {
        if (state.newSpaceName.isEmpty())
            return "Enter Space Name"
        return ""
    }

    fun logout() {
        state = state.copy(
            showLogoutDialog = false
        )
        firebaseHelper.logout()
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.clear()
        }
    }

    private fun search(query: String) {
        val results = realmHelper.getMySpaces(
            userId = state.userId,
            sortBy = state.sortByLabel,
            query = query,
            page = 1,
            limit = pageLimit,
            isFavList = state.isFavList
        )
        state = state.copy(
            mySpaces = state.mySpaces + results.filter { filterRes ->
                !state.mySpaces.contains(filterRes)
            }.sortSpaces(state.sortByLabel),
            searchResults = results
        )
    }

    private fun createSpace() {
        state = state.copy(
            isLoading = true,
            loadingMsg = "Creating Space"
        )
        viewModelScope.launch(Dispatchers.Main) {
            val calendar = Calendar.getInstance()
            val createdSpace = realmHelper.createSpace(
                spaceName = state.newSpaceName,
                userId = state.userId,
                time = calendar.time.time
            )
            delay(100)
            state = state.copy(
                isLoading = false,
                loadingMsg = null,
                newSpaceName = "",
                showCreationView = false,
            )
            if (createdSpace != null)
                state = state.copy(
                    mySpaces = state.mySpaces.toMutableList().apply {
                        add(createdSpace)
                    }.sortSpaces(state.sortByLabel)
                ) else {
                getSpaces()
            }
        }
    }

    private fun deleteSpace() {
        viewModelScope.launch(Dispatchers.Main) {
            realmHelper.deleteSpaces(state.mySpaces.mapNotNull { if (it.isSelected) it.id else null })
            delay(500)
            state = state.copy(
                mySpaces = state.mySpaces.filter { !it.isSelected }.sortSpaces(state.sortByLabel)
            )
        }
    }

    private fun getSpaces() {
        state = state.copy(
            page = 1,
            endReached = false
        )
        pagination.reset()
        val results = realmHelper.getMySpaces(
            userId = state.userId,
            sortBy = state.sortByLabel,
            page = state.page,
            limit = pageLimit,
            isFavList = state.isFavList
        )
        state = state.copy(
            mySpaces = results,
            searchResults = if (state.showSearchView) results.filter {
                it.name.contains(state.searchQuery, true)
            } else emptyList(),
            page = state.page + 1,
            endReached = results.size < pageLimit,
        )
    }

    private fun updateListViewPref(isListView: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.saveBoolean(DataStoreKey.SPACE_LIST_PREF, isListView)
        }
    }

    private fun updateSortByPref(sortBy: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.saveString(DataStoreKey.SPACE_SORT_PREF, sortBy)
        }
    }

    private fun updateSpaceDetailsData(parentId: Int?) {
        viewModelScope.launch(Dispatchers.Main) {
            val sortBy = dataStoreHelper.getString(
                DataStoreKey.SPACE_DETAILS_SORT_PREF,
                SortEnum.UPDATED_AT_DES.name
            )
            val folders = realmHelper.getFolderData(
                parentId = parentId,
                isSpaceParent = true,
                sortBy = sortBy,
                page = state.folderPage,
                limit = pageLimit
            )
            val files = realmHelper.getFiles(
                parentId = parentId,
                isSpaceParent = true,
                sortBy = sortBy,
                page = state.filePage,
                limit = pageLimit
            )
            state = state.copy(
                selectedParentID = parentId,
                isListView = dataStoreHelper.getBoolean(DataStoreKey.SPACE_DETAILS_LIST_PREF, true),
                sortByList = state.sortByList.map {
                    it.copy(isSelected = it.value == sortBy)
                },
                folderPage = state.folderPage + 1,
                folders = folders,
                isFolderEndReached = folders.size < pageLimit,
                filePage = state.filePage + 1,
                files = files,
                isFileEndReached = files.size < pageLimit
            )
            realmHelper.updateSpaceViewCount(parentId)
        }
    }

    private fun refreshFileData() {
        state = state.copy(
            filePage = 1,
            isFileEndReached = false
        )
        val files = realmHelper.getFiles(
            parentId = state.selectedParentID,
            isSpaceParent = true,
            sortBy = state.sortByLabel,
            page = state.filePage,
            limit = pageLimit,
            isFavList = state.isFavList,
        )
        state = state.copy(
            files = files,
            filePage = state.filePage + 1,
            isFileEndReached = files.size < pageLimit,
            fileSearchResults = if (state.showSearchView) files.filter {
                it.title.contains(state.searchQuery, true)
            } else emptyList()
        )
    }

}