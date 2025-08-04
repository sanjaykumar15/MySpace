package com.sanjay.myspace.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Login

@Serializable
data object MySpaceScreen

@Serializable
data class SpaceDetails(
    val parentId: Int?,
)

@Serializable
data class CardDetails(
    val fileId: Int?,
)