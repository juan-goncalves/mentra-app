package me.juangoncalves.mentra.failures

import androidx.annotation.StringRes

data class FleetingError(
    @StringRes val message: Int,
    val retryAction: (suspend () -> Unit)? = null
)
