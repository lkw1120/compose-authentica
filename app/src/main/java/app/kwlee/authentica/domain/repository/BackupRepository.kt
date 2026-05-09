package app.kwlee.authentica.domain.repository

interface BackupRepository {
    suspend fun exportEncryptedBackup(password: CharArray): String
    suspend fun importEncryptedBackup(json: String, password: CharArray): Int
}
