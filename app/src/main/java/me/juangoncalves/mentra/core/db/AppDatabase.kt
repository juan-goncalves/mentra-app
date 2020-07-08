package me.juangoncalves.mentra.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.juangoncalves.mentra.core.db.daos.CoinDao
import me.juangoncalves.mentra.core.db.models.CoinModel

@Database(entities = [CoinModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

}