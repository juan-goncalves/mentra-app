package me.juangoncalves.mentra.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.ui.common.DisplayError

fun Context.getThemeColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}


fun Context.createErrorSnackbar(error: DisplayError, anchor: View, duration: Int): Snackbar {
    val errorColor = getThemeColor(R.attr.colorError)
    val onErrorColor = getThemeColor(R.attr.colorOnError)

    return Snackbar.make(anchor, error.messageId, duration)
        .setBackgroundTint(errorColor)
        .setTextColor(onErrorColor)
        .setAction(R.string.retry) { error.retryAction() }
        .setActionTextColor(onErrorColor)
}