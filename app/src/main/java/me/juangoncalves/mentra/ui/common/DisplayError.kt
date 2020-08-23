package me.juangoncalves.mentra.ui.common

import androidx.annotation.StringRes

data class DisplayError(
    @StringRes val messageId: Int,
    val retryAction: () -> Unit
)