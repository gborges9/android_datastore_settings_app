package com.example.protodatastore

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.protodatastore.datastore.settings.UserSettingsManager
import com.example.protodatastore.userSettings.ThemeSettings
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var settingsManager: UserSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingsManager = UserSettingsManager(this)

        val lightThemeBtn = findViewById<MaterialRadioButton>(R.id.lightRadioBtn)
        val darkThemeBtn = findViewById<MaterialRadioButton>(R.id.darkRadioBtn)
        val autoThemeBtn = findViewById<MaterialRadioButton>(R.id.autoRadioBtn)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val notificationsSwitch = findViewById<SwitchMaterial>(R.id.notificationSwitch)

        // Listen to ThemeSettings changes and update the theme of the app and the selected radio button
        lifecycleScope.launch {
            settingsManager.themeMode.collect {
                setThemeMode(it)
                when(it){
                    ThemeSettings.Light -> lightThemeBtn.isChecked = true
                    ThemeSettings.Dark -> darkThemeBtn.isChecked = true
                    else -> autoThemeBtn.isChecked = true
                }
            }
        }

        // Listen to the notification settings, update the state of the switch and show a toast
        lifecycleScope.launch {
            settingsManager.notifications.collect {
                notificationsSwitch.isChecked = it
                Toast.makeText(this@MainActivity, "Notifications enabled: $it", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Update the datastore value when the radio button selection changes
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            lifecycleScope.launch {
                val mode = when(checkedId){
                    R.id.lightRadioBtn -> ThemeSettings.Light
                    R.id.darkRadioBtn -> ThemeSettings.Dark
                    else -> ThemeSettings.Auto
                }
                lifecycleScope.launch {
                    settingsManager.setThemeMode(mode)
                }
            }
        }

        // Update the datastore value when the switch button state changes
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsManager.setNotificationSettings(isChecked)
            }
        }
    }

    private fun setThemeMode(theme: ThemeSettings){
        val mode = when(theme){
            ThemeSettings.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeSettings.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }
}