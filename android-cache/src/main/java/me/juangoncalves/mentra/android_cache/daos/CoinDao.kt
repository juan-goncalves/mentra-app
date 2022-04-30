package me.juangoncalves.mentra.android_cache.daos

import androidx.room.*
import me.juangoncalves.mentra.android_cache.entities.CoinEntity

@Dao
interface CoinDao {

    @Query("SELECT * FROM Coin ORDER BY position ASC")
    suspend fun getAll(): List<CoinEntity>

    @Query("DELETE FROM Coin")
    suspend fun clearAll()

    @Query("SELECT * FROM Coin WHERE symbol = :symbol ORDER BY position ASC")
    suspend fun getCoinBySymbol(symbol: String): CoinEntity?

    @Update
    suspend fun update(coin: CoinEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(coin: CoinEntity): Long

    @Transaction
    suspend fun upsertAll(vararg coins: CoinEntity) {
        coins.forEach { coin ->
            val rowId = insert(coin)
            if (rowId == -1L) update(coin)
        }
    }
}