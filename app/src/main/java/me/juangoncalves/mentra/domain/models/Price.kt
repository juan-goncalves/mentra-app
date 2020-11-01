package me.juangoncalves.mentra.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Price(
    val currency: Currency,
    val value: Double,
    val date: LocalDateTime
) : Parcelable {
    companion object {
        val None: Price = Price(Currency.USD, -1.0, LocalDateTime.now())
    }
}