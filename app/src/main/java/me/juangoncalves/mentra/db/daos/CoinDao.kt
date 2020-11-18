package me.juangoncalves.mentra.db.daos

import androidx.room.*
import me.juangoncalves.mentra.db.models.CoinModel

@Dao
interface CoinDao {

    @Query("SELECT * FROM Coin")
    suspend fun getAll(): List<CoinModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg coins: CoinModel)

    @Query("DELETE FROM Coin")
    suspend fun clearAll()

    @Query("SELECT * FROM Coin WHERE symbol = :symbol")
    suspend fun getCoinBySymbol(symbol: String): CoinModel?

    @Update
    suspend fun update(coin: CoinModel)

}