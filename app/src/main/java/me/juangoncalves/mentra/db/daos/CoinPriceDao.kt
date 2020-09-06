package me.juangoncalves.mentra.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.juangoncalves.mentra.db.models.CoinPriceModel

@Dao
interface CoinPriceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinPrice(price: CoinPriceModel)

    @Query(
        """SELECT * from CoinPrice p, Coin c 
                    WHERE c.symbol = :coinSymbol 
                    AND p.coin_symbol = c.symbol
                    ORDER BY p.timestamp DESC"""
    )
    suspend fun getCoinPriceHistory(coinSymbol: String): List<CoinPriceModel>

    @Query("SELECT * FROM CoinPrice WHERE coin_symbol = :symbol ORDER BY timestamp DESC LIMIT 1")
    suspend fun getMostRecentCoinPrice(symbol: String): CoinPriceModel?

}