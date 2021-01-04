package me.juangoncalves.mentra.features.onboarding.currency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.OnboardingCurrencyItemBinding
import java.util.*

class CurrencyAdapter constructor(
    private val listener: Listener
) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    var data: List<Currency> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
            selectFirstCurrency(value)
        }

    private var lastSelectedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OnboardingCurrencyItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val currency = data[position]
        val context = root.context

        radioButton.isChecked = position == lastSelectedPos

        radioButton.text = context.getString(
            R.string.onboarding_currency,
            currency.getDisplayName(Locale.getDefault()),
            currency.currencyCode
        )

        radioButton.setOnClickListener {
            listener.onCurrencySelected(currency)
            val copyOfLastCheckedPosition = lastSelectedPos
            lastSelectedPos = position
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastSelectedPos)
        }
    }

    override fun getItemCount(): Int = data.size

    private fun selectFirstCurrency(value: List<Currency>) {
        if (value.isNotEmpty()) {
            lastSelectedPos = 0
            listener.onCurrencySelected(value[lastSelectedPos])
        }
    }

    inner class ViewHolder(val binding: OnboardingCurrencyItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface Listener {
        fun onCurrencySelected(currency: Currency)
    }

}