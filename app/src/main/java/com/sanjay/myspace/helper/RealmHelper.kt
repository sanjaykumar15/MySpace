package com.sanjay.myspace.helper

import com.sanjay.myspace.model.MySpaceData
import com.sanjay.myspace.model.RFiles
import com.sanjay.myspace.model.RFolders
import com.sanjay.myspace.model.RMySpaceData
import com.sanjay.myspace.model.SortEnum
import com.sanjay.myspace.model.SpaceFile
import com.sanjay.myspace.model.SpaceFolder
import com.sanjay.myspace.utils.ModelUtil.toMySpaceData
import com.sanjay.myspace.utils.ModelUtil.toMySpaceDataList
import com.sanjay.myspace.utils.ModelUtil.toSpaceFileList
import com.sanjay.myspace.utils.ModelUtil.toSpaceFiles
import com.sanjay.myspace.utils.ModelUtil.toSpaceFolder
import com.sanjay.myspace.utils.ModelUtil.toSpaceFolderList
import io.realm.Case
import io.realm.Realm
import io.realm.Sort

class RealmHelper(
    private var realm: Realm,
) {
    fun getTitle(id: Int?, isSpaceParent: Boolean): String {
        return if (isSpaceParent) getMySpaceDataById(id)?.name ?: "Space Details"
        else getFolderById(id)?.name ?: "Folder Details"
    }

    fun getMySpaces(
        userId: String,
        sortBy: String,
        page: Int,
        limit: Int,
        query: String? = null,
        isFavList: Boolean = false,
    ): List<MySpaceData> {
        var realmQuery = realm.where(RMySpaceData::class.java)
            .equalTo("userId", userId)

        if (isFavList) {
            realmQuery = realmQuery
                .and()
                .equalTo("isFavorite", true)
        }

        realmQuery = when (sortBy) {
            SortEnum.NAME_ASC.name -> {
                realmQuery.sort("name", Sort.ASCENDING)
            }

            SortEnum.NAME_DES.name -> {
                realmQuery.sort("name", Sort.DESCENDING)
            }

            SortEnum.CREATED_AT_ASC.name -> {
                realmQuery.sort("createdAt", Sort.ASCENDING)
            }

            SortEnum.CREATED_AT_DES.name -> {
                realmQuery.sort("createdAt", Sort.DESCENDING)
            }

            SortEnum.UPDATED_AT_ASC.name -> {
                realmQuery.sort("updatedAt", Sort.ASCENDING)
            }

            SortEnum.UPDATED_AT_DES.name -> {
                realmQuery.sort("updatedAt", Sort.DESCENDING)
            }

            SortEnum.COUNT_ASC.name -> {
                realmQuery.sort("viewCount", Sort.ASCENDING)
            }

            SortEnum.COUNT_DES.name -> {
                realmQuery.sort("viewCount", Sort.DESCENDING)
            }

            else -> {
                realmQuery.sort("updatedAt", Sort.DESCENDING)
            }
        }

        realmQuery = if (query.isNullOrEmpty()) {
            realmQuery.limit((page * limit).toLong())
        } else {
            realmQuery.and()
                .contains("name", query, Case.INSENSITIVE)
        }
        val startIndex = if (!query.isNullOrEmpty()) 0 else (page - 1) * limit

        val results = realmQuery.findAll()
        return results.slice(startIndex until results.size)
            .toMySpaceDataList()
    }

    fun getMySpaceDataById(id: Int?): RMySpaceData? {
        return realm.where(RMySpaceData::class.java)
            .equalTo("id", id)
            .findFirst()
    }

    fun getFolderData(
        parentId: Int?,
        isSpaceParent: Boolean,
        sortBy: String,
        page: Int,
        limit: Int,
        query: String? = null,
    ): List<SpaceFolder> {
        var realmQuery = realm
            .where(RFolders::class.java)
            .equalTo("parentId", parentId)
            .and()
            .equalTo("isSpaceParent", isSpaceParent)

        realmQuery = when (sortBy) {
            SortEnum.NAME_ASC.name -> {
                realmQuery.sort("name", Sort.ASCENDING)
            }

            SortEnum.NAME_DES.name -> {
                realmQuery.sort("name", Sort.DESCENDING)
            }

            SortEnum.CREATED_AT_ASC.name -> {
                realmQuery.sort("createdAt", Sort.ASCENDING)
            }

            SortEnum.CREATED_AT_DES.name -> {
                realmQuery.sort("createdAt", Sort.DESCENDING)
            }

            SortEnum.UPDATED_AT_ASC.name -> {
                realmQuery.sort("updatedAt", Sort.ASCENDING)
            }

            SortEnum.UPDATED_AT_DES.name -> {
                realmQuery.sort("updatedAt", Sort.DESCENDING)
            }

            SortEnum.COUNT_ASC.name -> {
                realmQuery.sort("viewCount", Sort.ASCENDING)
            }

            SortEnum.COUNT_DES.name -> {
                realmQuery.sort("viewCount", Sort.DESCENDING)
            }

            else -> {
                realmQuery.sort("updatedAt", Sort.DESCENDING)
            }
        }

        realmQuery = if (query.isNullOrEmpty()) {
            realmQuery.limit((page * limit).toLong())
        } else {
            realmQuery.and()
                .contains("name", query, Case.INSENSITIVE)
        }
        val startIndex = if (!query.isNullOrEmpty()) 0 else (page - 1) * limit

        val results = realmQuery.findAll()
        return results
            .slice(startIndex until results.size)
            .toSpaceFolderList()
    }

    fun getFolderById(id: Int?): RFolders? {
        return realm.where(RFolders::class.java)
            .equalTo("id", id)
            .findFirst()
    }

    fun getFiles(
        parentId: Int?,
        isSpaceParent: Boolean,
        sortBy: String,
        page: Int,
        limit: Int,
        query: String? = null,
        isFavList: Boolean = false,
    ): List<SpaceFile> {
        var realmQuery = realm.where(RFiles::class.java)
            .equalTo("parentId", parentId)
            .and()
            .equalTo("isSpaceParent", isSpaceParent)

        if (isFavList) {
            realmQuery = realmQuery
                .and()
                .equalTo("isFavorite", true)
        }

        realmQuery = when (sortBy) {
            SortEnum.NAME_ASC.name -> {
                realmQuery.sort("title", Sort.ASCENDING)
            }

            SortEnum.NAME_DES.name -> {
                realmQuery.sort("title", Sort.DESCENDING)
            }

            SortEnum.CREATED_AT_ASC.name -> {
                realmQuery.sort("createdAt", Sort.ASCENDING)
            }

            SortEnum.CREATED_AT_DES.name -> {
                realmQuery.sort("createdAt", Sort.DESCENDING)
            }

            SortEnum.UPDATED_AT_ASC.name -> {
                realmQuery.sort("updatedAt", Sort.ASCENDING)
            }

            SortEnum.UPDATED_AT_DES.name -> {
                realmQuery.sort("updatedAt", Sort.DESCENDING)
            }

            SortEnum.COUNT_ASC.name -> {
                realmQuery.sort("viewCount", Sort.ASCENDING)
            }

            SortEnum.COUNT_DES.name -> {
                realmQuery.sort("viewCount", Sort.DESCENDING)
            }

            else -> {
                realmQuery.sort("updatedAt", Sort.DESCENDING)
            }
        }

        realmQuery = if (query.isNullOrEmpty()) {
            realmQuery.limit((page * limit).toLong())
        } else {
            realmQuery.and()
                .contains("name", query, Case.INSENSITIVE)
        }
        val startIndex = if (!query.isNullOrEmpty()) 0 else (page - 1) * limit

        val results = realmQuery.findAll()
        return results
            .slice(startIndex until results.size)
            .toSpaceFileList()
    }

    fun getFileData(id: Int?): SpaceFile? {
        return realm.where(RFiles::class.java)
            .equalTo("id", id)
            .findFirst()
            ?.toSpaceFiles()
    }

    fun createSpace(spaceName: String, userId: String, time: Long): MySpaceData? {
        val spaceId = getLatestSpaceId()
        val data = RMySpaceData().apply {
            id = spaceId
            name = spaceName.trim()
            this.userId = userId
            createdAt = time
            updatedAt = time
        }
        realm.executeTransaction {
            it.insertOrUpdate(data)
        }
        return getMySpaceDataById(spaceId)?.toMySpaceData()
    }

    fun createFolder(
        parentId: Int?,
        folderName: String,
        time: Long,
        isSpaceParent: Boolean,
    ): SpaceFolder? {
        val folderId = getLatestFolderId()
        val data = RFolders().apply {
            id = folderId
            this.parentId = parentId
            this.isSpaceParent = isSpaceParent
            name = folderName.trim()
            createdAt = time
            updatedAt = time
        }
        realm.executeTransaction {
            it.insertOrUpdate(data)
        }
        updateTime(parentId, isSpaceParent, time)
        return getFolderById(folderId)?.toSpaceFolder()
    }

    fun createFile(
        parentId: Int?,
        name: String,
        time: Long,
        title: String,
        description: String,
        isSpaceParent: Boolean,
    ): SpaceFile? {
        val fileId = getLatestFileId()
        val data = RFiles().apply {
            id = fileId
            this.parentId = parentId
            fileName = name.trim()
            this.title = title.trim()
            this.description = description.trim()
            createdAt = time
            updatedAt = time
            this.isSpaceParent = isSpaceParent
        }
        realm.executeTransaction {
            it.insertOrUpdate(data)
        }
        updateTime(parentId, isSpaceParent, time)
        return getFileData(fileId)
    }

    fun updateFile(
        id: Int?,
        fileName: String,
        title: String,
        description: String,
        time: Long,
    ) {
        realm.executeTransaction {
            val fileData = it.where(RFiles::class.java)
                .equalTo("id", id)
                .findFirst()

            fileData?.apply {
                this.fileName = fileName.trim()
                this.title = title.trim()
                this.description = description.trim()
                updatedAt = time
            }?.let { file ->
                it.insertOrUpdate(file)
            }
        }
        val fileData = getFileData(id)
        updateTime(fileData?.parentId, fileData?.isSpaceParent ?: false, time)
    }

    fun updateFav(id: Int?, isFile: Boolean) {
        realm.executeTransaction {
            if (isFile) {
                val fileData = it.where(RFiles::class.java)
                    .equalTo("id", id)
                    .findFirst()
                fileData?.apply {
                    isFavorite = !isFavorite
                }?.let { file ->
                    it.insertOrUpdate(file)
                }
            } else {
                val spaceData = it.where(RMySpaceData::class.java)
                    .equalTo("id", id)
                    .findFirst()
                spaceData?.apply {
                    isFavorite = !isFavorite
                }?.let { file ->
                    it.insertOrUpdate(file)
                }
            }
        }
    }

    fun updateSpaceViewCount(id: Int?) {
        realm.executeTransaction { realm ->
            val spaceData = realm.where(RMySpaceData::class.java)
                .equalTo("id", id)
                .findFirst()
            spaceData?.apply {
                viewCount = getSpaceViewCount(id)
            }?.let {
                realm.insertOrUpdate(spaceData)
            }
        }
    }

    fun updateFolderViewCount(id: Int?) {
        val count = getFolderViewCount(id)
        realm.executeTransaction { realm ->
            val folderData = realm.where(RFolders::class.java)
                .equalTo("id", id)
                .findFirst()
            folderData?.apply {
                viewCount = count
            }?.let {
                realm.insertOrUpdate(folderData)
            }
        }
    }

    fun updateFileViewCount(id: Int?) {
        val count = getFileViewCount(id)
        realm.executeTransaction { realm ->
            val folderData = realm.where(RFiles::class.java)
                .equalTo("id", id)
                .findFirst()
            folderData?.apply {
                viewCount = count
            }?.let {
                realm.insertOrUpdate(folderData)
            }
        }
    }

    private fun updateTime(parentId: Int?, isSpaceParent: Boolean, time: Long) {
        realm.executeTransaction {
            var parentId = parentId
            if (isSpaceParent) {
                val spaceData = it.where(RMySpaceData::class.java)
                    .equalTo("id", parentId)
                    .findFirst()
                spaceData?.apply {
                    updatedAt = time
                    it.insertOrUpdate(spaceData)
                }
            } else {
                while (true) {
                    val parentData = it.where(RFolders::class.java)
                        .equalTo("id", parentId)
                        .findFirst()
                    if (parentData != null) {
                        parentData.updatedAt = time
                        it.insertOrUpdate(parentData)
                        if (parentData.isSpaceParent) {
                            val spaceData = it.where(RMySpaceData::class.java)
                                .equalTo("id", parentData.parentId)
                                .findFirst()
                            spaceData?.apply {
                                updatedAt = time
                                it.insertOrUpdate(spaceData)
                            }
                            break
                        }
                        parentId = parentData.parentId
                    } else break
                }
            }
        }
    }

    fun deleteSpaces(ids: List<Int?>) {
        realm.executeTransaction {
            val spaces = it.where(RMySpaceData::class.java)
                .`in`("id", ids.toTypedArray())
                .findAll()

            val folders = it.where(RFolders::class.java)
                .`in`("parentId", ids.toTypedArray())
                .and()
                .equalTo("isSpaceParent", true)
                .findAll()

            var parentIds = folders.map { fol -> fol.id }
            while (true) {
                val subFolders = it.where(RFolders::class.java)
                    .`in`("parentId", parentIds.toTypedArray())
                    .and()
                    .equalTo("isSpaceParent", false)
                    .findAll()
                if (subFolders.isNotEmpty()) {
                    it.where(RFiles::class.java)
                        .`in`("parentId", parentIds.toTypedArray())
                        .and()
                        .equalTo("isSpaceParent", false)
                        .findAll()
                        .deleteAllFromRealm()
                    parentIds = subFolders.map { fol -> fol.id }
                    subFolders.deleteAllFromRealm()
                } else {
                    break
                }
            }

            it.where(RFiles::class.java)
                .`in`("parentId", ids.toTypedArray())
                .and()
                .equalTo("isSpaceParent", true)
                .findAll()
                .deleteAllFromRealm()

            it.where(RFiles::class.java)
                .`in`("parentId", parentIds.toTypedArray())
                .and()
                .equalTo("isSpaceParent", false)
                .findAll()
                .deleteAllFromRealm()
            folders.deleteAllFromRealm()
            spaces.deleteAllFromRealm()
        }
    }

    fun deleteFolders(ids: List<Int?>) {
        realm.executeTransaction {
            val folders = it.where(RFolders::class.java)
                .`in`("id", ids.toTypedArray())
                .findAll()

            var parentIds = ids
            while (true) {
                val subFolders = it.where(RFolders::class.java)
                    .`in`("parentId", parentIds.toTypedArray())
                    .and()
                    .equalTo("isSpaceParent", false)
                    .findAll()
                if (subFolders.isNotEmpty()) {
                    it.where(RFiles::class.java)
                        .`in`("parentId", parentIds.toTypedArray())
                        .and()
                        .equalTo("isSpaceParent", false)
                        .findAll()
                        .deleteAllFromRealm()
                    parentIds = subFolders.map { fol -> fol.id }
                    subFolders.deleteAllFromRealm()
                } else {
                    break
                }
            }

            val filesParentIds = ids + folders.map { fol -> fol.id }
            it.where(RFiles::class.java)
                .`in`("parentId", filesParentIds.toTypedArray())
                .and()
                .equalTo("isSpaceParent", false)
                .findAll()
                .deleteAllFromRealm()
            folders.deleteAllFromRealm()
        }
    }

    fun deleteFiles(ids: List<Int?>) {
        realm.executeTransaction {
            it.where(RFiles::class.java)
                .`in`("id", ids.toTypedArray())
                .findAll()
                .deleteAllFromRealm()
        }
    }

    private fun getLatestSpaceId(): Int {
        return realm.where(RMySpaceData::class.java)
            .sort("id", Sort.DESCENDING)
            .findFirst()
            ?.id?.let {
                it + 1
            } ?: 1
    }

    private fun getLatestFolderId(): Int {
        return realm.where(RFolders::class.java)
            .sort("id", Sort.DESCENDING)
            .findFirst()
            ?.id?.let {
                it + 1
            } ?: 1
    }

    private fun getLatestFileId(): Int {
        return realm.where(RFiles::class.java)
            .sort("id", Sort.DESCENDING)
            .findFirst()
            ?.id?.let {
                it + 1
            } ?: 1
    }

    private fun getSpaceViewCount(id: Int?): Int {
        return realm.where(RMySpaceData::class.java)
            .equalTo("id", id)
            .sort("id", Sort.DESCENDING)
            .findFirst()
            ?.viewCount?.let {
                it + 1
            } ?: 1
    }

    private fun getFolderViewCount(id: Int?): Int {
        return realm.where(RFolders::class.java)
            .equalTo("id", id)
            .sort("id", Sort.DESCENDING)
            .findFirst()
            ?.viewCount?.let {
                it + 1
            } ?: 1
    }

    private fun getFileViewCount(id: Int?): Int {
        return realm.where(RFiles::class.java)
            .equalTo("id", id)
            .sort("id", Sort.DESCENDING)
            .findFirst()
            ?.viewCount?.let {
                it + 1
            } ?: 1
    }
}