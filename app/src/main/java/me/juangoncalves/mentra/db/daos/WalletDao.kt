package me.juangoncalves.mentra.db.daos

import androidx.room.*
import me.juangoncalves.mentra.db.models.WalletModel

@Dao
interface WalletDao {

    @Query("SELECT * FROM Wallet")
    suspend fun getAll(): List<WalletModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg wallets: WalletModel)

    @Query("SELECT * FROM Wallet WHERE coin_symbol = :symbol")
    suspend fun findByCoin(symbol: String): List<WalletModel>

    @Delete
    suspend fun delete(wallet: WalletModel)

}
