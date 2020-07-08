package me.juangoncalves.mentra.core.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.juangoncalves.mentra.core.db.models.CoinModel
import me.juangoncalves.mentra.core.db.models.CoinPriceModel

@Dao
interface CoinDao {

    @Query("SELECT * FROM Coin")
    suspend fun getAll(): List<CoinModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg coins: CoinModel)

    @Query("DELETE FROM Coin")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinPrice(price: CoinPriceModel)

    @Query(
        """SELECT * from CoinPriceModel p, Coin c 
                    WHERE c.symbol = :coinSymbol 
                    AND p.coin_symbol = c.symbol
                    ORDER BY p.timestamp DESC"""
    )
    suspend fun getCoinPriceHistory(coinSymbol: String): List<CoinPriceModel>

}