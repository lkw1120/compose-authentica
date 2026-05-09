package app.kwlee.authentica.model.repository

import app.kwlee.authentica.domain.model.OtpAccount
import app.kwlee.authentica.domain.model.OtpAlgorithm
import app.kwlee.authentica.domain.repository.OtpRepository
import app.kwlee.authentica.model.crypto.SecretCipher
import app.kwlee.authentica.model.local.OtpAccountDao
import app.kwlee.authentica.model.local.OtpAccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OtpRepositoryImpl @Inject constructor(
    private val dao: OtpAccountDao,
    private val secretCipher: SecretCipher
) : OtpRepository {

    override fun observeAccounts(): Flow<List<OtpAccount>> {
        return dao.observeAccounts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getNextSortOrder(): Int = dao.getNextSortOrder()

    override suspend fun addAccount(account: OtpAccount) {
        dao.insert(account.toEntity())
    }

    override suspend fun deleteAccount(id: String) {
        dao.deleteById(id)
    }

    override suspend fun updateAccountLabel(id: String, issuer: String, accountName: String) {
        dao.updateLabel(
            id = id,
            issuer = issuer,
            accountName = accountName,
            label = "$issuer:$accountName",
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun reorderAccounts(idsInOrder: List<String>) {
        dao.reorderAccounts(idsInOrder)
    }

    private fun OtpAccountEntity.toDomain(): OtpAccount {
        return OtpAccount(
            id = id,
            issuer = issuer,
            accountName = accountName,
            label = label,
            secret = secretCipher.decrypt(encryptedSecret, encryptionIv),
            digits = digits,
            period = period,
            algorithm = runCatching { OtpAlgorithm.valueOf(algorithm) }.getOrDefault(OtpAlgorithm.SHA1),
            sortOrder = sortOrder,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun OtpAccount.toEntity(): OtpAccountEntity {
        val (encryptedSecret, iv) = secretCipher.encrypt(secret)
        return OtpAccountEntity(
            id = id,
            issuer = issuer,
            accountName = accountName,
            label = label,
            encryptedSecret = encryptedSecret,
            encryptionIv = iv,
            digits = digits,
            period = period,
            algorithm = algorithm.name,
            sortOrder = sortOrder,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
