package app.kwlee.authentica.model.local.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsDataStore {
    val REQUIRE_LAUNCH_AUTH = booleanPreferencesKey("require_launch_auth")
    val THEME_MODE = stringPreferencesKey("theme_mode")
}
