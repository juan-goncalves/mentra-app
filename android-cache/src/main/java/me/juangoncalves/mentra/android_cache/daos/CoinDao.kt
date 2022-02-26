package me.juangoncalves.mentra.android_cache.daos

import androidx.room.*
import me.juangoncalves.mentra.android_cache.models.CoinModel

@Dao
interface CoinDao {

    @Query("SELECT * FROM Coin ORDER BY position ASC")
    suspend fun getAll(): List<CoinModel>

    @Query("DELETE FROM Coin")
    suspend fun clearAll()

    @Query("SELECT * FROM Coin WHERE symbol = :symbol ORDER BY position ASC")
    suspend fun getCoinBySymbol(symbol: String): CoinModel?

    @Update
    suspend fun update(coin: CoinModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(coin: CoinModel): Long

    @Transaction
    suspend fun upsertAll(vararg coins: CoinModel) {
        coins.forEach { coin ->
            val rowId = insert(coin)
            if (rowId == -1L) update(coin)
        }
    }
}