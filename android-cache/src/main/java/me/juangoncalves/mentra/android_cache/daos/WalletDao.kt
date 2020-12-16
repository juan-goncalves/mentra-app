package me.juangoncalves.mentra.android_cache.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.android_cache.models.WalletModel

@Dao
interface WalletDao {

    @Query("SELECT * FROM Wallet")
    fun getWalletsStream(): Flow<List<WalletModel>>

    @Query("SELECT * FROM Wallet")
    suspend fun getAll(): List<WalletModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg wallets: WalletModel)

    @Query("SELECT * FROM Wallet WHERE coin_symbol = :symbol")
    suspend fun findByCoin(symbol: String): List<WalletModel>

    @Query("SELECT * FROM WALLET WHERE id = :id")
    suspend fun findById(id: Long): WalletModel?

    @Delete
    suspend fun delete(wallet: WalletModel)

    @Update
    suspend fun update(wallet: WalletModel)

}
