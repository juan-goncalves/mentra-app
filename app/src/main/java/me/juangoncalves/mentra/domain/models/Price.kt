package me.juangoncalves.mentra.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Parcelize
data class Price(
    val value: BigDecimal,
    val currency: Currency,
    val timestamp: LocalDateTime
) : Parcelable {

    companion object {
        val None: Price = Price(BigDecimal(-1), Currency.getInstance("USD"), LocalDateTime.now())
    }

}