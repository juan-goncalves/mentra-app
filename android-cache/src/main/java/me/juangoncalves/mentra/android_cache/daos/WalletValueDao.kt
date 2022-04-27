package me.juangoncalves.mentra.android_cache.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.juangoncalves.mentra.android_cache.entities.WalletValueEntity

@Dao
interface WalletValueDao {

    @Query("SELECT * FROM WalletValue WHERE wallet_id = :walletId ORDER BY date DESC")
    suspend fun getWalletValueHistory(walletId: Long): List<WalletValueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: WalletValueEntity)

}