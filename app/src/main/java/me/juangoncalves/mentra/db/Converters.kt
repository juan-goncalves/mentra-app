package me.juangoncalves.mentra.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun longToTimestamp(value: Long?): LocalDateTime? {
        if (value == null) return null
        return LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC)
    }

    @TypeConverter
    fun timestampToLong(value: LocalDateTime?): Long? {
        if (value == null) return null
        return value.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun dateToLong(value: LocalDate?): Long? {
        if (value == null) return null
        return value.toEpochDay()
    }

    @TypeConverter
    fun longToDate(value: Long?): LocalDate? {
        if (value == null) return null
        return LocalDate.ofEpochDay(value)
    }

}