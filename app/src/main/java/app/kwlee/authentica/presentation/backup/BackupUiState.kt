package app.kwlee.authentica.presentation.backup

enum class PasswordStrengthLabel { WEAK, MEDIUM, STRONG }

data class PasswordStrength(
    val level: Int = 0,
    val progress: Float = 0f,
    val label: PasswordStrengthLabel = PasswordStrengthLabel.WEAK
)

data class BackupUiState(
    val showExportPasswordSheet: Boolean = false,
    val exportPassword: String = "",
    val exportPasswordConfirm: String = "",
    val exportPasswordStrength: PasswordStrength = PasswordStrength(),
    val showImportPasswordSheet: Boolean = false,
    val importPassword: String = "",
    val isBackupLoading: Boolean = false
) {
    val canExport: Boolean
        get() = exportPassword.length >= 8 && exportPassword == exportPasswordConfirm

    val canImport: Boolean
        get() = importPassword.isNotBlank()
}
