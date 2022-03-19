package me.juangoncalves.mentra.android_cache

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

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
    fun bigDecimalToString(value: BigDecimal?): String? = value?.toString()

    @TypeConverter
    fun stringToBigDecimal(value: String?): BigDecimal? {
        if (value == null) return null
        return BigDecimal(value)
    }

    @TypeConverter
    fun currencyToString(value: Currency?): String? = value?.currencyCode

    @TypeConverter
    fun stringToCurrency(value: String?): Currency? {
        if (value == null) return null
        return Currency.getInstance(value)
    }
}