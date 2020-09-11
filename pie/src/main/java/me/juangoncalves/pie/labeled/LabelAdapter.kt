package me.juangoncalves.pie.labeled

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import me.juangoncalves.pie.R
import me.juangoncalves.pie.labeled.LabelAdapter.MyViewHolder

internal class LabelAdapter : RecyclerView.Adapter<MyViewHolder>() {

    private var dataset: Array<LabelItem> = emptyArray()

    internal class MyViewHolder(view: View) : ViewHolder(view) {
        var pieceLabelTextView: TextView = view.findViewById(R.id.pieceLabelTextView)
        var dotImageView: ImageView = view.findViewById(R.id.dotImageView)
    }

    fun updateDataSet(dataset: Array<LabelItem>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.pie_piece_label, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataset[position]
        holder.apply {
            pieceLabelTextView.text = currentItem.text
            dotImageView.setColorFilter(currentItem.color, PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun getItemCount(): Int = dataset.size

}