package me.juangoncalves.mentra.features.onboarding.currency

import android.content.Context
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.common.SingleChoiceAdapter
import java.util.*

class CurrencyAdapter(listener: Listener<Currency>) : SingleChoiceAdapter<Currency>(listener) {

    override fun getTextForItem(context: Context, item: Currency): String {
        return context.getString(
            R.string.onboarding_currency,
            item.getDisplayName(Locale.getDefault()),
            item.currencyCode,
        ).titleCase()
    }

    private fun String.titleCase(): String = replaceFirstChar { it.titlecase(Locale.getDefault()) }
}