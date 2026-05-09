package app.kwlee.authentica.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface OtpAccountDao {
    @Query("SELECT * FROM otp_accounts ORDER BY sortOrder ASC, createdAt ASC")
    fun observeAccounts(): Flow<List<OtpAccountEntity>>

    @Query("SELECT * FROM otp_accounts ORDER BY sortOrder ASC, createdAt ASC")
    suspend fun observeAccountsSnapshot(): List<OtpAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: OtpAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<OtpAccountEntity>)

    @Query("DELETE FROM otp_accounts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE otp_accounts SET issuer = :issuer, accountName = :accountName, label = :label, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateLabel(id: String, issuer: String, accountName: String, label: String, updatedAt: Long)

    @Query("SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM otp_accounts")
    suspend fun getNextSortOrder(): Int

    @Query("UPDATE otp_accounts SET sortOrder = :sortOrder, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSortOrder(id: String, sortOrder: Int, updatedAt: Long)

    @Transaction
    suspend fun reorderAccounts(idsInOrder: List<String>) {
        val now = System.currentTimeMillis()
        idsInOrder.forEachIndexed { index, id ->
            updateSortOrder(id = id, sortOrder = index, updatedAt = now)
        }
    }
}
