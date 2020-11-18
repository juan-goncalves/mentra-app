package me.juangoncalves.mentra.db

import androidx.room.TypeConverter
import me.juangoncalves.mentra.domain.models.IconType
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

    @TypeConverter
    fun iconTypeToInt(value: IconType?): Int? {
        if (value == null) return null
        return value.ordinal
    }

    @TypeConverter
    fun intToIconType(value: Int?): IconType? {
        if (value == null) return null
        return when (value) {
            0 -> IconType.Gradient
            1 -> IconType.Regular
            2 -> IconType.Unknown
            else -> null
        }
    }
}