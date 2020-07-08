package me.juangoncalves.mentra.core.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.juangoncalves.mentra.core.db.models.CoinModel

@Dao
interface CoinDao {

    @Query("SELECT * FROM Coin")
    suspend fun getAll(): List<CoinModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg coins: CoinModel)

    @Query("DELETE FROM Coin")
    suspend fun clearAll()

}