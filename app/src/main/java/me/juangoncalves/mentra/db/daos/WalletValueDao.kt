package me.juangoncalves.mentra.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.juangoncalves.mentra.db.models.WalletValueModel

@Dao
interface WalletValueDao {

    @Query("SELECT * FROM WalletValue WHERE wallet_id = :walletId ORDER BY date DESC")
    suspend fun getWalletValueHistory(walletId: Long): List<WalletValueModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: WalletValueModel)

}