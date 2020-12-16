package me.juangoncalves.mentra.android_cache.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.android_cache.models.PortfolioValueModel

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM PortfolioValue ORDER BY date DESC LIMIT 1")
    fun getPortfolioValueStream(): Flow<PortfolioValueModel?>

    @Query("SELECT * FROM PortfolioValue ORDER BY date ASC")
    fun getPortfolioHistoricValuesStream(): Flow<List<PortfolioValueModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(value: PortfolioValueModel)

}