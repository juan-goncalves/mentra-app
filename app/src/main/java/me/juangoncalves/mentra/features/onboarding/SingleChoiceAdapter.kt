package me.juangoncalves.mentra.features.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.juangoncalves.mentra.databinding.SingleChoiceItemBinding

abstract class SingleChoiceAdapter<T> constructor(
    private val listener: Listener<T>
) : RecyclerView.Adapter<SingleChoiceAdapter<T>.ViewHolder>() {

    var data: List<T> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
            selectFirstOption(value)
        }

    private var lastSelectedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleChoiceItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val currency = data[position]
        radioButton.isChecked = position == lastSelectedPos
        radioButton.text = getTextForItem(root.context, currency)

        radioButton.setOnClickListener {
            listener.onOptionSelected(currency)
            val copyOfLastCheckedPosition = lastSelectedPos
            lastSelectedPos = position
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastSelectedPos)
        }
    }

    abstract fun getTextForItem(context: Context, item: T): String

    override fun getItemCount(): Int = data.size

    private fun selectFirstOption(value: List<T>) {
        if (value.isNotEmpty()) {
            lastSelectedPos = 0
            listener.onOptionSelected(value[lastSelectedPos])
        }
    }

    inner class ViewHolder(val binding: SingleChoiceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface Listener<T> {
        fun onOptionSelected(option: T)
    }

}