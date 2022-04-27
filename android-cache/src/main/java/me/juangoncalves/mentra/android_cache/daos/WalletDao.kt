package me.juangoncalves.mentra.android_cache.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.android_cache.entities.WalletEntity

@Dao
interface WalletDao {

    @Query("SELECT * FROM Wallet")
    fun getWalletsStream(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM Wallet")
    suspend fun getAll(): List<WalletEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg wallets: WalletEntity)

    @Query("SELECT * FROM Wallet WHERE coin_symbol = :symbol")
    suspend fun findByCoin(symbol: String): List<WalletEntity>

    @Query("SELECT * FROM WALLET WHERE id = :id")
    suspend fun findById(id: Long): WalletEntity?

    @Delete
    suspend fun delete(wallet: WalletEntity)

    @Update
    suspend fun update(wallet: WalletEntity)

}
