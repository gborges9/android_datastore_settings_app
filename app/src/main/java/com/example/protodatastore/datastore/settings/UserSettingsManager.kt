package com.example.protodatastore.datastore.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.protodatastore.userSettings.ThemeSettings
import com.example.protodatastore.userSettings.UserSettings
import kotlinx.coroutines.flow.map

class UserSettingsManager(context: Context) {
    private val datastore = context.settingsDataStore

    val settings get() = datastore.data
    val themeMode get() = datastore.data.map { it.theme }
    val notifications = datastore.data.map { it.notifications }

    suspend fun setSettings(settings: UserSettings){
        datastore.updateData { settings }
    }

    suspend fun setThemeMode(themeSettings: ThemeSettings){
        datastore.updateData {
            it.toBuilder().setTheme(themeSettings).build()
        }
    }

    suspend fun setNotificationSettings(notifications: Boolean){
        datastore.updateData {
            it.toBuilder().setNotifications(notifications).build()
        }
    }

    companion object{
        private val Context.settingsDataStore: DataStore<UserSettings> by dataStore(
            fileName = "settings.pb",
            serializer = UserSettingsSerializer
        )
    }
}