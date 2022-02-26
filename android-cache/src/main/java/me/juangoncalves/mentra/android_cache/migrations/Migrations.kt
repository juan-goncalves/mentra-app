package me.juangoncalves.mentra.android_cache.migrations

import androidx.room.migration.Migration
import java.lang.Integer.MAX_VALUE

internal object Migrations {

    val all get() = arrayOf(MIGRATION_17_18)

    private val MIGRATION_17_18 = Migration(17, 18) { database ->
        database.execSQL("ALTER TABLE Coin ADD COLUMN position INTEGER NOT NULL DEFAULT $MAX_VALUE")
    }
}