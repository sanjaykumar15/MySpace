package com.sanjay.myspace.model

data class SelectionItem(
    val label: String,
    val value: String,
    val isSelected: Boolean = false
)