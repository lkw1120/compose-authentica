package app.kwlee.authentica.model.repository

import app.kwlee.authentica.domain.model.OtpAlgorithm
import app.kwlee.authentica.domain.repository.BackupRepository
import app.kwlee.authentica.model.backup.SecureBackupCodec
import app.kwlee.authentica.model.crypto.SecretCipher
import app.kwlee.authentica.model.local.OtpAccountDao
import app.kwlee.authentica.model.local.OtpAccountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val dao: OtpAccountDao,
    private val secretCipher: SecretCipher
) : BackupRepository {

    override suspend fun exportEncryptedBackup(password: CharArray): String {
        return withContext(Dispatchers.IO) {
            val plainAccounts = dao.observeAccountsSnapshot().map { entity ->
                SecureBackupCodec.PlainAccount(
                    issuer = entity.issuer,
                    accountName = entity.accountName,
                    secret = secretCipher.decrypt(entity.encryptedSecret, entity.encryptionIv),
                    digits = entity.digits,
                    period = entity.period,
                    algorithm = runCatching { OtpAlgorithm.valueOf(entity.algorithm) }.getOrDefault(OtpAlgorithm.SHA1)
                )
            }
            SecureBackupCodec.encrypt(plainAccounts, password)
        }
    }

    override suspend fun importEncryptedBackup(json: String, password: CharArray): Int {
        return withContext(Dispatchers.IO) {
            val plainAccounts = SecureBackupCodec.decrypt(json, password)
            val now = System.currentTimeMillis()
            val newEntities = plainAccounts.mapIndexed { index, item ->
                val trimmedIssuer = item.issuer.trim()
                val trimmedName = item.accountName.trim()
                val trimmedSecret = item.secret.trim()
                val (encryptedSecret, iv) = secretCipher.encrypt(trimmedSecret)
                OtpAccountEntity(
                    id = UUID.randomUUID().toString(),
                    issuer = trimmedIssuer,
                    accountName = trimmedName,
                    label = "$trimmedIssuer:$trimmedName",
                    encryptedSecret = encryptedSecret,
                    encryptionIv = iv,
                    digits = item.digits.coerceIn(6, 8),
                    period = item.period.takeIf { it > 0 } ?: 30,
                    algorithm = item.algorithm.name,
                    sortOrder = Int.MAX_VALUE - plainAccounts.size + index,
                    createdAt = now,
                    updatedAt = now
                )
            }
            dao.insertAll(newEntities)
            newEntities.size
        }
    }
}
