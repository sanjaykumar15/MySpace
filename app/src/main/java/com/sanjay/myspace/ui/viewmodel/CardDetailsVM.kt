package com.sanjay.myspace.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjay.myspace.helper.RealmHelper
import com.sanjay.myspace.ui.event.CardDetailsEvents
import com.sanjay.myspace.ui.state.CardDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CardDetailsVM @Inject constructor(
    private val realmHelper: RealmHelper,
) : ViewModel() {

    var state by mutableStateOf(CardDetailsState())
        private set

    fun eventHandler(event: CardDetailsEvents) {
        when (event) {
            is CardDetailsEvents.OnInit -> {
                state = state.copy(
                    isInit = false,
                    fileId = event.fileId,
                    fileData = realmHelper.getFileData(event.fileId),
                )
                realmHelper.updateFileViewCount(event.fileId)
            }

            is CardDetailsEvents.HandleFileUpdateView -> {
                state = state.copy(
                    showFileUpdateView = event.show,
                    fileName = state.fileData?.fileName ?: "",
                    fileTitle = state.fileData?.title ?: "",
                    fileDes = state.fileData?.description ?: ""
                )
            }

            is CardDetailsEvents.OnFileNameChanged -> {
                state = state.copy(
                    fileName = event.name
                )
            }

            is CardDetailsEvents.OnFileTitleChanged -> {
                state = state.copy(
                    fileTitle = event.title
                )
            }

            is CardDetailsEvents.OnFileDesChanged -> {
                state = state.copy(
                    fileDes = event.description
                )
            }

            is CardDetailsEvents.OnUpdateClicked -> {
                updateFile()
            }

            CardDetailsEvents.OnFavoriteClick -> {
                updateFav()
            }

            is CardDetailsEvents.HandleShareOption -> {
                state = state.copy(
                    showShareOption = event.show
                )
            }

            else -> {}
        }
    }

    fun fileValidation(): String {
        if (state.fileName.trim().isEmpty())
            return "Enter File Name"
        if (state.fileTitle.trim().isEmpty())
            return "Enter File Title"
        if (state.fileDes.trim().isEmpty())
            return "Enter File Description"
        return ""
    }

    private fun updateFile() {
        viewModelScope.launch(Dispatchers.Main) {
            val calendar = Calendar.getInstance()
            realmHelper.updateFile(
                id = state.fileId,
                fileName = state.fileName,
                title = state.fileTitle,
                description = state.fileDes,
                time = calendar.time.time
            )
            delay(500)
            val fileData = realmHelper.getFileData(state.fileId)
            state = state.copy(
                showFileUpdateView = false,
                fileData = fileData,
                fileName = "",
                fileTitle = "",
                fileDes = ""
            )
        }
    }

    private fun updateFav() {
        viewModelScope.launch(Dispatchers.Main) {
            realmHelper.updateFav(
                id = state.fileId,
                isFile = true
            )
            delay(100)
            state = state.copy(
                fileData = realmHelper.getFileData(state.fileId)
            )
        }
    }

}