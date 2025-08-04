package com.sanjay.myspace.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjay.myspace.helper.DataStoreHelper
import com.sanjay.myspace.helper.DataStoreKey
import com.sanjay.myspace.helper.RealmHelper
import com.sanjay.myspace.model.SortEnum
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.model.SpaceFolder
import com.sanjay.myspace.paginator.PaginatorImpl
import com.sanjay.myspace.ui.event.SpaceDetailsEvents
import com.sanjay.myspace.ui.state.SpaceDetailsState
import com.sanjay.myspace.utils.ModelUtil.sortSpaceFiles
import com.sanjay.myspace.utils.ModelUtil.sortSpaceFolders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SpaceDetailsVM @Inject constructor(
    private val realmHelper: RealmHelper,
    private val dataStoreHelper: DataStoreHelper,
) : ViewModel() {

    var state by mutableStateOf(SpaceDetailsState())
        private set

    private val pageLimit: Int = 10

    @Suppress("UNCHECKED_CAST")
    private val pagination = PaginatorImpl(
        initialKey = 2,
        onLoadUpdated = {
            state = state.copy(
                isPageRefreshing = it
            )
        },
        getNextKey = {
            if (!state.isFolderEndReached)
                state.folderPage + 1
            else
                state.filePage + 1
        },
        onRequest = { nextPage ->
            if (!state.isFolderEndReached) {
                realmHelper.getFolderData(
                    parentId = state.parentIds.peek(),
                    isSpaceParent = state.parentIds.size <= 1,
                    sortBy = state.sortByLabel,
                    page = state.folderPage,
                    limit = pageLimit
                )
            } else {
                realmHelper.getFiles(
                    parentId = state.parentIds.peek(),
                    isSpaceParent = state.parentIds.size <= 1,
                    sortBy = state.sortByLabel,
                    page = state.filePage,
                    limit = pageLimit,
                    isFavList = state.isFavList
                )
            }
        },
        onSuccess = { items, newKey ->
            if (!state.isFolderEndReached) {
                if (items.all { SpaceFolder::class.java.isInstance(it) }) {
                    state = state.copy(
                        folders = state.folders + items.filter {
                            !state.folders.contains(it)
                        } as List<SpaceFolder>,
                        folderPage = newKey,
                        isFolderEndReached = items.size < pageLimit
                    )
                }
            } else {
                if (items.all { SpaceFile::class.java.isInstance(it) }) {
                    state = state.copy(
                        files = state.files + items.filter {
                            !state.files.contains(it)
                        } as List<SpaceFile>,
                        filePage = newKey,
                        isFileEndReached = items.size < pageLimit
                    )
                }
            }
        }
    )

    private fun setDatastoreData(parentId: Int?) {
        viewModelScope.launch(Dispatchers.Main) {
            val sortBy = dataStoreHelper.getString(
                DataStoreKey.SPACE_DETAILS_SORT_PREF,
                SortEnum.UPDATED_AT_DES.name
            )
            val folders = realmHelper.getFolderData(
                parentId = parentId,
                isSpaceParent = state.parentIds.size <= 1,
                sortBy = sortBy,
                page = state.folderPage,
                limit = pageLimit
            )
            val files = realmHelper.getFiles(
                parentId = parentId,
                isSpaceParent = state.parentIds.size <= 1,
                sortBy = sortBy,
                page = state.filePage,
                limit = pageLimit
            )
            state = state.copy(
                isInit = false,
                isSpaceParent = true,
                parentIds = state.parentIds.apply {
                    push(parentId)
                },
                isListView = dataStoreHelper.getBoolean(DataStoreKey.SPACE_DETAILS_LIST_PREF, true),
                sortByList = state.sortByList.map {
                    it.copy(isSelected = it.value == sortBy)
                },
                title = realmHelper.getTitle(parentId, true),
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

    fun eventHandler(event: SpaceDetailsEvents) {
        when (event) {
            is SpaceDetailsEvents.UpdateDSData -> {
                setDatastoreData(event.parentId)
            }

            is SpaceDetailsEvents.OnInit -> {
                state = state.copy(
                    isSpaceParent = if (event.isBack) state.parentIds.size <= 1 else false,
                    parentIds = state.parentIds.apply {
                        if (!event.isBack)
                            push(event.parentId)
                    },
                    title = realmHelper.getTitle(
                        id = if (event.isBack) state.parentIds.peek() else event.parentId,
                        isSpaceParent = state.parentIds.size <= 1
                    )
                )
                updateData(
                    parentId = if (event.isBack) state.parentIds.peek() else event.parentId,
                    isSpaceParent = state.parentIds.size <= 1
                )
                if (event.isUpdate) {
                    realmHelper.updateFolderViewCount(event.parentId)
                }
            }

            SpaceDetailsEvents.OnRefresh -> {
                updateData(
                    parentId = state.parentIds.peek(),
                    isSpaceParent = state.isSpaceParent
                )
            }

            is SpaceDetailsEvents.HandleSelectionView -> {
                state = state.copy(
                    showSelectionView = event.show,
                    showSearchView = false,
                    searchQuery = "",
                    folderSearchResults = emptyList(),
                    fileSearchResults = emptyList()
                )
            }

            is SpaceDetailsEvents.HandleFolderCreationView -> {
                state = state.copy(
                    showFolderCreateView = event.show,
                    newFolderName = "",
                    showSearchView = false,
                    searchQuery = "",
                    folderSearchResults = emptyList(),
                    fileSearchResults = emptyList()
                )
            }

            is SpaceDetailsEvents.HandleFileCreationView -> {
                state = state.copy(
                    showFileCreateView = event.show,
                    newFileName = "",
                    fileTitle = "",
                    fileDes = "",
                    showSearchView = false,
                    searchQuery = "",
                    folderSearchResults = emptyList(),
                    fileSearchResults = emptyList()
                )
            }

            is SpaceDetailsEvents.OnFolderNameChanged -> {
                state = state.copy(
                    newFolderName = event.name
                )
            }

            is SpaceDetailsEvents.OnFileNameChanged -> {
                state = state.copy(
                    newFileName = event.name
                )
            }

            is SpaceDetailsEvents.OnFileTitleChanged -> {
                state = state.copy(
                    fileTitle = event.title
                )
            }

            is SpaceDetailsEvents.OnFileDesChanged -> {
                state = state.copy(
                    fileDes = event.description
                )
            }

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

            is SpaceDetailsEvents.HandleSortByView -> {
                state = state.copy(
                    showSortByView = event.show
                )
            }

            is SpaceDetailsEvents.OnSortByClicked -> {
                state = state.copy(
                    showSortByView = false,
                    sortByList = state.sortByList.map {
                        it.copy(isSelected = it.value == event.sortBy)
                    },
                    folders = state.folders.sortSpaceFolders(event.sortBy),
                    files = state.files.sortSpaceFiles(event.sortBy),
                )
                updateSortByPref(event.sortBy)
            }

            is SpaceDetailsEvents.OnCreateClicked -> {
                if (event.isFolderCreation) createFolder()
                else createFile()
            }

            SpaceDetailsEvents.OnDeleteClicked -> {
                deleteItems()
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

    fun folderNameValidation(): String {
        if (state.newFolderName.trim().isEmpty())
            return "Enter Folder Name"
        return ""
    }

    fun fileValidation(): String {
        if (state.newFileName.trim().isEmpty())
            return "Enter File Name"
        if (state.fileTitle.trim().isEmpty())
            return "Enter File Title"
        if (state.fileDes.trim().isEmpty())
            return "Enter File Description"
        return ""
    }

    private fun search(query: String) {
        val folders = realmHelper.getFolderData(
            parentId = state.parentIds.peek(),
            isSpaceParent = state.isSpaceParent,
            sortBy = state.sortByLabel,
            query = query,
            page = 1,
            limit = pageLimit
        )
        val files = realmHelper.getFiles(
            parentId = state.parentIds.peek(),
            isSpaceParent = state.isSpaceParent,
            query = query,
            sortBy = state.sortByLabel,
            page = 1,
            limit = pageLimit,
            isFavList = state.isFavList
        )
        state = state.copy(
            folders = state.folders + folders.filter {
                !state.folders.contains(it)
            }.sortSpaceFolders(state.sortByLabel),
            folderSearchResults = folders,
            files = state.files + files.filter {
                !state.files.contains(it)
            }.sortSpaceFiles(state.sortByLabel),
            fileSearchResults = files
        )
    }

    private fun updateData(
        parentId: Int?,
        isSpaceParent: Boolean,
    ) {
        state = state.copy(
            folderPage = 1,
            filePage = 1,
            isFolderEndReached = false,
            isFileEndReached = false
        )
        pagination.reset()
        val folders = realmHelper.getFolderData(
            parentId = parentId,
            isSpaceParent = isSpaceParent,
            sortBy = state.sortByLabel,
            page = state.folderPage,
            limit = pageLimit
        )
        val isFolderEndReached = folders.size < pageLimit
        val files = if (isFolderEndReached || state.showFolders == false) {
            realmHelper.getFiles(
                parentId = parentId,
                isSpaceParent = isSpaceParent,
                sortBy = state.sortByLabel,
                page = state.filePage,
                limit = pageLimit,
                isFavList = state.isFavList
            )
        } else emptyList()
        state = state.copy(
            folders = folders,
            isFolderEndReached = isFolderEndReached,
            files = files,
            isFileEndReached = files.size < pageLimit
        )
    }

    private fun refreshFileData() {
        state = state.copy(
            filePage = 1,
            isFileEndReached = false
        )
        val files = realmHelper.getFiles(
            parentId = state.parentIds.peek(),
            isSpaceParent = state.isSpaceParent,
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

    private fun createFolder() {
        viewModelScope.launch(Dispatchers.Main) {
            val calendar = Calendar.getInstance()
            val createdFolder = realmHelper.createFolder(
                parentId = state.parentIds.peek(),
                folderName = state.newFolderName,
                time = calendar.time.time,
                isSpaceParent = state.isSpaceParent
            )
            delay(500)
            state = state.copy(
                newFolderName = "",
                showSelectionView = false,
                showFolderCreateView = false,
            )
            if (createdFolder != null) {
                state = state.copy(
                    folders = state.folders.toMutableList().apply {
                        add(createdFolder)
                    }.sortSpaceFolders(state.sortByLabel)
                )
            } else {
                updateData(
                    parentId = state.parentIds.peek(),
                    isSpaceParent = state.isSpaceParent
                )
            }
        }
    }

    private fun createFile() {
        viewModelScope.launch(Dispatchers.Main) {
            val calendar = Calendar.getInstance()
            val createdFile = realmHelper.createFile(
                parentId = state.parentIds.peek(),
                name = state.newFileName,
                title = state.fileTitle,
                description = state.fileDes,
                time = calendar.time.time,
                isSpaceParent = state.isSpaceParent
            )
            delay(500)
            state = state.copy(
                newFileName = "",
                fileTitle = "",
                fileDes = "",
                showSelectionView = false,
                showFileCreateView = false,
            )
            if (createdFile != null) {
                state = state.copy(
                    files = state.files.toMutableList().apply {
                        add(createdFile)
                    }.sortSpaceFiles(state.sortByLabel)
                )
            } else {
                updateData(
                    parentId = state.parentIds.peek(),
                    isSpaceParent = state.isSpaceParent
                )
            }
        }
    }

    private fun deleteItems() {
        viewModelScope.launch(Dispatchers.Main) {
            if (state.folders.any { it.isSelected }) {
                realmHelper.deleteFolders(state.folders.mapNotNull { if (it.isSelected) it.id else null })
            }
            if (state.files.any { it.isSelected }) {
                realmHelper.deleteFiles(state.files.mapNotNull { if (it.isSelected) it.id else null })
            }
            delay(500)
            state = state.copy(
                folders = state.folders.filter { !it.isSelected },
                files = state.files.filter { !it.isSelected }
            )
        }
    }

    private fun updateListViewPref(isListView: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.saveBoolean(DataStoreKey.SPACE_DETAILS_LIST_PREF, isListView)
        }
    }

    private fun updateSortByPref(sortBy: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreHelper.saveString(DataStoreKey.SPACE_DETAILS_SORT_PREF, sortBy)
        }
    }
}