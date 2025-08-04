package com.sanjay.myspace.model

data class MySpaceData(
    val id: Int?,
    val name: String,
    val createdAt: String,
    val updatedAt: String,
    val viewCount: Int,
    val isSelected: Boolean = false,
    val isFavorite: Boolean = false,
)

data class SpaceFolder(
    val id: Int?,
    val parentId: Int?,
    val name: String,
    val viewCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val isSelected: Boolean = false,
)

data class SpaceFile(
    val id: Int?,
    val parentId: Int?,
    val fileName: String,
    val title: String,
    val viewCount: Int,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val isFavorite: Boolean = false,
    val isSpaceParent: Boolean = false,
    val isSelected: Boolean = false,
)