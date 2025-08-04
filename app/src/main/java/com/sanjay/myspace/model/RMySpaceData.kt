package com.sanjay.myspace.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RMySpaceData : RealmObject() {
    @PrimaryKey
    var id: Int? = null
    var userId: String? = null
    var name: String? = null
    var createdAt: Long? = null
    var updatedAt: Long? = null
    var viewCount: Int? = null
    var isFavorite: Boolean = false
}

open class RFolders : RealmObject() {
    @PrimaryKey
    var id: Int? = null
    var parentId: Int? = null
    var isSpaceParent: Boolean = false
    var name: String? = null
    var files: RealmList<RFiles>? = null
    var createdAt: Long? = null
    var updatedAt: Long? = null
    var viewCount: Int? = null
}

open class RFiles : RealmObject() {
    @PrimaryKey
    var id: Int? = null
    var parentId: Int? = null
    var isSpaceParent: Boolean = false
    var isFavorite: Boolean = false
    var fileName: String? = null
    var title: String? = null
    var description: String? = null
    var createdAt: Long? = null
    var updatedAt: Long? = null
    var viewCount: Int? = null
}