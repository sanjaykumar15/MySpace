package com.sanjay.myspace.ui.state

import com.sanjay.myspace.model.SelectionItem
import com.sanjay.myspace.model.SpaceFile

data class CardDetailsState(
    val isInit: Boolean = true,
    val fileId: Int? = null,
    val showFileUpdateView: Boolean = false,
    val fileData: SpaceFile? = null,
    val fileName: String = "",
    val fileTitle: String = "",
    val fileDes: String = "",
    val showShareOption: Boolean = false,
) {
    val shareOptions: List<SelectionItem> = listOf(
        SelectionItem(
            label = "Share only content",
            value = ShareOptions.ONLY_CONTENT.name,
            isSelected = false
        ),
        SelectionItem(
            label = "Share entire screen",
            value = ShareOptions.ENTIRE_SCREEN.name,
            isSelected = false
        ),
    )
}

enum class ShareOptions {
    ONLY_CONTENT,
    ENTIRE_SCREEN
}