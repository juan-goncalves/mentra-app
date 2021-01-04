package me.juangoncalves.mentra.features.onboarding.periodic_refresh

import android.content.Context
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.features.onboarding.SingleChoiceAdapter
import java.time.Duration

class DurationAdapter(listener: Listener<Duration>) : SingleChoiceAdapter<Duration>(listener) {

    override fun getTextForItem(context: Context, item: Duration): String {
        return when (val hours = item.toHours()) {
            24L -> context.getString(R.string.once_a_day)
            else -> context.getString(R.string.every_x_hours, hours)
        }
    }

}