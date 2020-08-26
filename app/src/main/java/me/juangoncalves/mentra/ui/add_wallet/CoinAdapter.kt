package me.juangoncalves.mentra.ui.add_wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.CoinRecommendationItemBinding

class CoinAdapter(data: List<DisplayCoin>) : RecyclerView.Adapter<CoinAdapter.ViewHolder>() {

    var data: List<DisplayCoin> = data
        set(value) {
            val diffResult = DiffUtil.calculateDiff(CoinDiffCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    var selectedCoin: DisplayCoin? = null

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CoinRecommendationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayCoin = data[position]
        holder.binding.apply {
            nameTextView.text = displayCoin.coin.name
            if (displayCoin == selectedCoin) {
                radioButton.post { radioButton.isChecked = true }
            } else {
                radioButton.isChecked = false
            }

            Glide.with(root)
                .load(displayCoin.coin.imageUrl)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.coin_placeholder)
                .into(coinImageView)

            root.setOnClickListener {
                selectedCoin = displayCoin
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(val binding: CoinRecommendationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class CoinDiffCallback(
    private val old: List<DisplayCoin>,
    private val next: List<DisplayCoin>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = next.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].coin.symbol == next[newItemPosition].coin.symbol
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == next[newItemPosition]
    }

}