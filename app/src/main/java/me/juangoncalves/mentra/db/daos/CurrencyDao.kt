package me.juangoncalves.mentra.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.juangoncalves.mentra.db.models.CurrencyEntity
import me.juangoncalves.mentra.db.models.ExchangeRateEntity

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM Currency")
    suspend fun getCurrencies(): List<CurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCurrencies(vararg currency: CurrencyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(vararg rates: ExchangeRateEntity)

    @Query(
        """
        SELECT * 
        FROM ExchangeRate 
        WHERE base_currency_symbol=:baseSymbol AND target_currency_symbol=:targetSymbol"""
    )
    suspend fun getExchangeRate(baseSymbol: String, targetSymbol: String): ExchangeRateEntity?

}