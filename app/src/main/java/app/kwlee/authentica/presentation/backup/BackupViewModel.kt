package app.kwlee.authentica.presentation.backup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState = _uiState.asStateFlow()

    private var pendingExportPassword: CharArray? = null

    fun setLoading(loading: Boolean) {
        _uiState.update { it.copy(isBackupLoading = loading) }
    }

    fun openExportSheet() {
        _uiState.update { it.copy(showExportPasswordSheet = true) }
    }

    fun dismissExportSheet() {
        _uiState.update {
            it.copy(
                showExportPasswordSheet = false,
                exportPassword = "",
                exportPasswordConfirm = "",
                exportPasswordStrength = passwordStrength("")
            )
        }
    }

    fun updateExportPassword(value: String) {
        _uiState.update {
            it.copy(
                exportPassword = value,
                exportPasswordStrength = passwordStrength(value)
            )
        }
    }

    fun updateExportPasswordConfirm(value: String) {
        _uiState.update { it.copy(exportPasswordConfirm = value) }
    }

    fun stageExportPassword(): Boolean {
        val state = _uiState.value
        if (!state.canExport) return false
        clearPendingExportPassword()
        pendingExportPassword = state.exportPassword.toCharArray()
        dismissExportSheet()
        return true
    }

    fun consumeStagedExportPassword(): CharArray? {
        val value = pendingExportPassword ?: return null
        pendingExportPassword = null
        return value
    }

    fun clearStagedExportPassword() {
        clearPendingExportPassword()
    }

    fun openImportSheet() {
        _uiState.update { it.copy(showImportPasswordSheet = true) }
    }

    fun dismissImportSheet() {
        _uiState.update {
            it.copy(
                showImportPasswordSheet = false,
                importPassword = ""
            )
        }
    }

    fun updateImportPassword(value: String) {
        _uiState.update { it.copy(importPassword = value) }
    }

    fun consumeImportPassword(): CharArray? {
        val state = _uiState.value
        if (!state.canImport) return null
        val value = state.importPassword.toCharArray()
        dismissImportSheet()
        return value
    }

    private fun passwordStrength(password: String): PasswordStrength {
        if (password.isEmpty()) {
            return PasswordStrength(level = 0, progress = 0f, label = PasswordStrengthLabel.WEAK)
        }

        return when {
            password.length >= 16 -> PasswordStrength(level = 3, progress = 1f, label = PasswordStrengthLabel.STRONG)
            password.length >= 12 -> PasswordStrength(level = 2, progress = 0.66f, label = PasswordStrengthLabel.MEDIUM)
            else -> PasswordStrength(level = 1, progress = 0.33f, label = PasswordStrengthLabel.WEAK)
        }
    }

    private fun clearPendingExportPassword() {
        pendingExportPassword?.fill('\u0000')
        pendingExportPassword = null
    }
}
