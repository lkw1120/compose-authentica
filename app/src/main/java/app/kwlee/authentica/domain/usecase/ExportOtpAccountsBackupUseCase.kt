package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.repository.BackupRepository
import javax.inject.Inject

class ExportOtpAccountsBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(password: CharArray): String {
        return backupRepository.exportEncryptedBackup(password)
    }
}
