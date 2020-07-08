package me.juangoncalves.mentra.core.db

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun toDateTime(value: Long?): LocalDateTime? {
        if (value == null) return null
        return LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC)
    }

    @TypeConverter
    fun toTimestamp(value: LocalDateTime?): Long? {
        if (value == null) return null
        return value.toEpochSecond(ZoneOffset.UTC)
    }

}