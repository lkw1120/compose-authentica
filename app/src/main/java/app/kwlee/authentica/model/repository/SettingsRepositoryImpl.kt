package app.kwlee.authentica.model.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.kwlee.authentica.domain.model.AppThemeMode
import app.kwlee.authentica.domain.repository.SettingsRepository
import app.kwlee.authentica.model.local.settings.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun observeRequireLaunchAuth(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[SettingsDataStore.REQUIRE_LAUNCH_AUTH] ?: false
        }
    }

    override suspend fun setRequireLaunchAuth(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[SettingsDataStore.REQUIRE_LAUNCH_AUTH] = enabled
        }
    }

    override fun observeThemeMode(): Flow<AppThemeMode> {
        return dataStore.data.map { prefs ->
            val raw = prefs[SettingsDataStore.THEME_MODE] ?: AppThemeMode.SYSTEM.name
            runCatching { AppThemeMode.valueOf(raw) }.getOrDefault(AppThemeMode.SYSTEM)
        }
    }

    override suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { prefs ->
            prefs[SettingsDataStore.THEME_MODE] = mode.name
        }
    }
}
