package com.co.pesinari

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MyPreferences(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    companion object {
        val EMAIL = stringPreferencesKey("user_email")
        private var mInstance: MyPreferences? = null

        @Synchronized
        fun getInstance(context: Context): MyPreferences? {
                if (MyPreferences.mInstance == null) {
                    MyPreferences.mInstance = MyPreferences(context)
                }
                return MyPreferences.mInstance
        }
    }

    suspend fun savetoDataStore(email:String) {
        context.dataStore.edit {
            it[EMAIL] = email
        }
    }

    fun getFromDataStore(): Flow<String> = context.dataStore.data.map {
        it[EMAIL] ?: ""
    }
}