package me.juangoncalves.mentra.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.juangoncalves.mentra.db.models.WalletModel

@Dao
interface WalletDao {

    @Query("SELECT * FROM Wallet")
    suspend fun getAll(): List<WalletModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg wallets: WalletModel)

}
