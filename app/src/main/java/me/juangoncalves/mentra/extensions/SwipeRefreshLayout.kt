package me.juangoncalves.mentra.extensions

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import me.juangoncalves.mentra.R

fun SwipeRefreshLayout.styleByTheme() = apply {
    context?.let { context ->
        setColorSchemeColors(
            context.getThemeColor(R.attr.colorSecondary),
            context.getThemeColor(R.attr.colorSecondaryVariant),
            context.getThemeColor(R.attr.colorPrimary),
            context.getThemeColor(R.attr.colorPrimaryDark)
        )
    }
}