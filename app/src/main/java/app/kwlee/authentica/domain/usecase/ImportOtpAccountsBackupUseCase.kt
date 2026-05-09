package app.kwlee.authentica.domain.usecase

import app.kwlee.authentica.domain.repository.BackupRepository
import javax.inject.Inject

class ImportOtpAccountsBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
) {
    suspend operator fun invoke(json: String, password: CharArray): Int {
        return backupRepository.importEncryptedBackup(json, password)
    }
}
