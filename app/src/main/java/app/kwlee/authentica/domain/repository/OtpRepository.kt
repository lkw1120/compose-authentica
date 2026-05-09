package app.kwlee.authentica.domain.repository

import app.kwlee.authentica.domain.model.OtpAccount
import kotlinx.coroutines.flow.Flow

interface OtpRepository {
    fun observeAccounts(): Flow<List<OtpAccount>>
    suspend fun getNextSortOrder(): Int
    suspend fun addAccount(account: OtpAccount)
    suspend fun deleteAccount(id: String)
    suspend fun updateAccountLabel(id: String, issuer: String, accountName: String)
    suspend fun reorderAccounts(idsInOrder: List<String>)
}
