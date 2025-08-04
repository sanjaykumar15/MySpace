package com.sanjay.myspace.utils

import com.sanjay.myspace.model.MySpaceData
import com.sanjay.myspace.model.RFiles
import com.sanjay.myspace.model.RFolders
import com.sanjay.myspace.model.RMySpaceData
import com.sanjay.myspace.model.SelectionItem
import com.sanjay.myspace.model.SortEnum
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.model.SpaceFolder

object ModelUtil {

    fun List<RMySpaceData>.toMySpaceDataList(): List<MySpaceData> {
        return this.map {
            it.toMySpaceData()
        }
    }

    fun RMySpaceData.toMySpaceData(): MySpaceData {
        return MySpaceData(
            id = id,
            name = name ?: "N/A",
            viewCount = viewCount ?: 0,
            createdAt = createdAt?.let {
                DateFormatConstants.uiDateFormat.format(it).uppercase()
            } ?: "N/A",
            updatedAt = updatedAt?.let {
                DateFormatConstants.uiDateFormat.format(it).uppercase()
            } ?: "N/A",
            isFavorite = isFavorite
        )
    }

    fun List<MySpaceData>.sortSpaces(sortBy: String): List<MySpaceData> {
        return when (sortBy) {
            SortEnum.NAME_ASC.name -> {
                this.sortedBy { it.name }
            }

            SortEnum.NAME_DES.name -> {
                this.sortedByDescending { it.name }
            }

            SortEnum.CREATED_AT_ASC.name -> {
                this.sortedBy { it.createdAt }
            }

            SortEnum.CREATED_AT_DES.name -> {
                this.sortedByDescending { it.createdAt }
            }

            SortEnum.UPDATED_AT_ASC.name -> {
                this.sortedBy { it.updatedAt }
            }

            SortEnum.UPDATED_AT_DES.name -> {
                this.sortedByDescending { it.updatedAt }
            }

            SortEnum.COUNT_ASC.name -> {
                this.sortedBy { it.viewCount }
            }

            SortEnum.COUNT_DES.name -> {
                this.sortedByDescending { it.viewCount }
            }

            else -> {
                this.sortedByDescending { it.updatedAt }
            }
        }
    }

    fun List<RFolders>?.toSpaceFolderList(): List<SpaceFolder> {
        return this?.map {
            it.toSpaceFolder()
        } ?: emptyList()
    }

    fun RFolders.toSpaceFolder(): SpaceFolder {
        return SpaceFolder(
            id = id,
            parentId = parentId,
            name = name ?: "N/A",
            createdAt = createdAt?.let {
                DateFormatConstants.uiDateFormat.format(it).uppercase()
            } ?: "N/A",
            updatedAt = updatedAt?.let {
                DateFormatConstants.uiDateFormat.format(it).uppercase()
            } ?: "N/A",
            viewCount = viewCount ?: 0
        )
    }

    fun List<SpaceFolder>.sortSpaceFolders(sortBy: String): List<SpaceFolder> {
        return when (sortBy) {
            SortEnum.NAME_ASC.name -> {
                this.sortedBy { it.name }
            }

            SortEnum.NAME_DES.name -> {
                this.sortedByDescending { it.name }
            }

            SortEnum.CREATED_AT_ASC.name -> {
                this.sortedBy { it.createdAt }
            }

            SortEnum.CREATED_AT_DES.name -> {
                this.sortedByDescending { it.createdAt }
            }

            SortEnum.UPDATED_AT_ASC.name -> {
                this.sortedBy { it.updatedAt }
            }

            SortEnum.UPDATED_AT_DES.name -> {
                this.sortedByDescending { it.updatedAt }
            }

            SortEnum.COUNT_ASC.name -> {
                this.sortedBy { it.viewCount }
            }

            SortEnum.COUNT_DES.name -> {
                this.sortedByDescending { it.viewCount }
            }

            else -> {
                this.sortedByDescending { it.updatedAt }
            }
        }
    }

    fun List<RFiles>?.toSpaceFileList(): List<SpaceFile> {
        return this?.map {
            it.toSpaceFiles()
        } ?: emptyList()
    }

    fun List<SpaceFile>.sortSpaceFiles(sortBy: String): List<SpaceFile> {
        return when (sortBy) {
            SortEnum.NAME_ASC.name -> {
                this.sortedBy { it.title }
            }

            SortEnum.NAME_DES.name -> {
                this.sortedByDescending { it.title }
            }

            SortEnum.CREATED_AT_ASC.name -> {
                this.sortedBy { it.createdAt }
            }

            SortEnum.CREATED_AT_DES.name -> {
                this.sortedByDescending { it.createdAt }
            }

            SortEnum.UPDATED_AT_ASC.name -> {
                this.sortedBy { it.updatedAt }
            }

            SortEnum.UPDATED_AT_DES.name -> {
                this.sortedByDescending { it.updatedAt }
            }

            SortEnum.COUNT_ASC.name -> {
                this.sortedBy { it.viewCount }
            }

            SortEnum.COUNT_DES.name -> {
                this.sortedByDescending { it.viewCount }
            }

            else -> {
                this.sortedByDescending { it.updatedAt }
            }
        }
    }

    fun RFiles.toSpaceFiles(): SpaceFile {
        return SpaceFile(
            id = id,
            parentId = parentId,
            fileName = fileName ?: "N/A",
            title = title ?: "N/A",
            description = description ?: " N/A",
            isSpaceParent = isSpaceParent,
            isFavorite = isFavorite,
            createdAt = createdAt?.let {
                DateFormatConstants.uiDateFormat.format(it).uppercase()
            } ?: "N/A",
            updatedAt = updatedAt?.let {
                DateFormatConstants.uiDateFormat.format(it).uppercase()
            } ?: "N/A",
            viewCount = viewCount ?: 0
        )
    }

    fun getSortByItems(): List<SelectionItem> {
        return SortEnum.entries.map {
            SelectionItem(
                label = getSortLabel(it),
                value = it.name,
                isSelected = false
            )
        }
    }

    fun getSortLabel(item: SortEnum): String {
        return when (item) {
            SortEnum.NAME_ASC -> "Name ASC"
            SortEnum.NAME_DES -> "Name DES"
            SortEnum.CREATED_AT_ASC -> "Created At ASC"
            SortEnum.CREATED_AT_DES -> "Created At DES"
            SortEnum.UPDATED_AT_ASC -> "Updated At ASC"
            SortEnum.UPDATED_AT_DES -> "Updated At DES"
            SortEnum.COUNT_ASC -> "View Count ASC"
            SortEnum.COUNT_DES -> "View Count DES"
        }
    }

}