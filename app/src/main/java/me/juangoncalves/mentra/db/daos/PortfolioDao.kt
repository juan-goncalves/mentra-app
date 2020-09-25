package me.juangoncalves.mentra.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.db.models.PortfolioValueModel

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM PortfolioValue ORDER BY date DESC LIMIT 1")
    fun getPortfolioValue(): Flow<PortfolioValueModel>

    @Query("SELECT * FROM PortfolioValue ORDER BY date ASC")
    fun getPortfolioValueHistory(): Flow<List<PortfolioValueModel>>

    @Query("SELECT * FROM PortfolioValue ORDER BY date DESC LIMIT 1")
    suspend fun getLatestPortfolioValue(): PortfolioValueModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(value: PortfolioValueModel)

    @Query("SELECT * FROM PortfolioValue")
    suspend fun getValueHistory(): List<PortfolioValueModel>

}