package com.sanjay.myspace.utils

import kotlinx.coroutines.flow.MutableSharedFlow

object RefreshEventHandler {
    val eventFlow: MutableSharedFlow<RefreshEventItem?> = MutableSharedFlow()
}

enum class RefreshEventItem {
    REFRESH_SPACE,
    REFRESH_FOLDER,
    REFRESH_FILE
}