package com.seiuh.smartroomapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Tạo extension property để lấy DataStore (Singleton)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_PASSWORD = stringPreferencesKey("password")
        val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    // Lấy thông tin đã lưu
    val userPreferencesFlow: Flow<UserCredentials?> = context.dataStore.data
        .map { preferences ->
            val remember = preferences[KEY_REMEMBER_ME] ?: false
            if (remember) {
                UserCredentials(
                    username = preferences[KEY_USERNAME] ?: "",
                    password = preferences[KEY_PASSWORD] ?: "",
                    rememberMe = true
                )
            } else {
                null
            }
        }

    // Lưu thông tin
    suspend fun saveCredentials(username: String, pass: String, remember: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_REMEMBER_ME] = remember
            if (remember) {
                preferences[KEY_USERNAME] = username
                preferences[KEY_PASSWORD] = pass
            } else {
                // Nếu không chọn nhớ thì xóa đi cho an toàn
                preferences.remove(KEY_USERNAME)
                preferences.remove(KEY_PASSWORD)
            }
        }
    }

    // Xóa thông tin (Khi Logout)
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}

// Data class nhỏ để hứng dữ liệu
data class UserCredentials(
    val username: String,
    val password: String,
    val rememberMe: Boolean
)