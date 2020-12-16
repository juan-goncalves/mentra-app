package me.juangoncalves.mentra.android_cache.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.android_cache.models.CoinPriceModel

@Dao
interface CoinPriceDao {

    @Query(
        """
        SELECT * FROM CoinPrice p, Wallet w
        WHERE p.coin_symbol = w.coin_symbol AND 
              p.timestamp = (SELECT MAX(timestamp) FROM CoinPrice WHERE coin_symbol = p.coin_symbol)
        GROUP BY p.coin_symbol
        """
    )
    fun getActiveCoinPricesStream(): Flow<List<CoinPriceModel>>

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