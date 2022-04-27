package me.juangoncalves.mentra.android_cache.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.android_cache.entities.PortfolioValueEntity

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM PortfolioValue ORDER BY date DESC LIMIT 1")
    fun getPortfolioValueStream(): Flow<PortfolioValueEntity?>

    @Query("SELECT * FROM PortfolioValue ORDER BY date ASC")
    fun getPortfolioHistoricValuesStream(): Flow<List<PortfolioValueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(value: PortfolioValueEntity)

}