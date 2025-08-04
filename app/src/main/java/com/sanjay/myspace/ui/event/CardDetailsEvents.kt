package com.sanjay.myspace.ui.event

import android.graphics.Bitmap

sealed class CardDetailsEvents {
    data object OnBackPressed : CardDetailsEvents()
    data object OnUpdateClicked : CardDetailsEvents()
    data object OnFavoriteClick : CardDetailsEvents()

    data class ShowMsg(
        val msg: String,
    ) : CardDetailsEvents()

    data class HandleShareOption(
        val show: Boolean,
    ) : CardDetailsEvents()

    data class OnInit(
        val fileId: Int?,
    ) : CardDetailsEvents()

    data class HandleFileUpdateView(
        val show: Boolean,
    ) : CardDetailsEvents()

    data class OnFileNameChanged(
        val name: String,
    ) : CardDetailsEvents()

    data class OnFileTitleChanged(
        val title: String,
    ) : CardDetailsEvents()

    data class OnFileDesChanged(
        val description: String,
    ) : CardDetailsEvents()

    data class ShareImage(
        val bitmap: Bitmap,
    ) : CardDetailsEvents()
}