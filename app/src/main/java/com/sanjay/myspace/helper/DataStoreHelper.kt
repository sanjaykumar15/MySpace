package com.sanjay.myspace.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sanjay.myspace.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

typealias StringKey = Preferences.Key<String>
typealias BooleanKey = Preferences.Key<Boolean>

class DataStoreHelper(context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DataStoreCons.NAME)

    private val dataStore = context.dataStore

    suspend fun saveString(key: StringKey, value: String?) {
        dataStore.edit {
            it[key] = value ?: ""
        }
    }

    suspend fun getString(key: StringKey, defaultValue: String = ""): String {
        return dataStore.data.map {
            it[key]
        }.first() ?: defaultValue
    }

    fun getStringAsFlow(key: StringKey): Flow<String?> {
        return dataStore.data.map {
            it[key]
        }
    }

    suspend fun saveBoolean(key: BooleanKey, value: Boolean) {
        dataStore.edit {
            it[key] = value
        }
    }

    suspend fun getBoolean(key: BooleanKey, defaultValue: Boolean = false): Boolean {
        return dataStore.data.map {
            it[key]
        }.first() ?: defaultValue
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }

}

object DataStoreKey {
    val USER_ID = stringPreferencesKey("userId")
    val USER_EMAIL = stringPreferencesKey("email")
    val SPACE_LIST_PREF = booleanPreferencesKey("isSpaceListView")
    val SPACE_SORT_PREF = stringPreferencesKey("spaceSort")
    val SPACE_DETAILS_LIST_PREF = booleanPreferencesKey("isSpaceDetailsListView")
    val SPACE_DETAILS_SORT_PREF = stringPreferencesKey("spaceDetailsSort")
}