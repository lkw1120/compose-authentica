package app.kwlee.authentica.domain.repository

import app.kwlee.authentica.domain.model.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeRequireLaunchAuth(): Flow<Boolean>
    suspend fun setRequireLaunchAuth(enabled: Boolean)
    fun observeThemeMode(): Flow<AppThemeMode>
    suspend fun setThemeMode(mode: AppThemeMode)
}
