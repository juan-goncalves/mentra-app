package me.juangoncalves.mentra.features.common

import androidx.annotation.StringRes

data class DisplayError(
    @StringRes val messageId: Int,
    val retryAction: () -> Unit
)