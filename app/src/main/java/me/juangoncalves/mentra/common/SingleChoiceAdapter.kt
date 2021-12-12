package me.juangoncalves.mentra.common

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

    private var lastSelectedIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleChoiceItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val currency = data[holder.adapterPosition]
        radioButton.isChecked = holder.adapterPosition == lastSelectedIndex
        radioButton.text = getTextForItem(root.context, currency)

        radioButton.setOnClickListener {
            listener.onOptionSelected(currency)
            val copyOfLastCheckedPosition = lastSelectedIndex
            lastSelectedIndex = holder.adapterPosition
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastSelectedIndex)
        }
    }

    abstract fun getTextForItem(context: Context, item: T): String

    override fun getItemCount(): Int = data.size

    private fun selectFirstOption(value: List<T>) {
        if (value.isNotEmpty()) {
            lastSelectedIndex = 0
            listener.onOptionSelected(value[lastSelectedIndex])
        }
    }

    inner class ViewHolder(val binding: SingleChoiceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface Listener<T> {
        fun onOptionSelected(option: T)
    }

}