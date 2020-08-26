package me.juangoncalves.mentra.ui.wallet_creation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.CoinRecommendationItemBinding

class CoinAdapter : RecyclerView.Adapter<CoinAdapter.ViewHolder>() {

    private val differ: AsyncListDiffer<DisplayCoin> = AsyncListDiffer(this, CoinItemCallback())

    var selectedCoin: DisplayCoin? = null
        private set

    override fun getItemCount() = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CoinRecommendationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayCoin = differ.currentList[position];
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

    fun submitList(data: List<DisplayCoin>) = differ.submitList(data)

    inner class ViewHolder(val binding: CoinRecommendationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class CoinItemCallback : DiffUtil.ItemCallback<DisplayCoin>() {
    override fun areItemsTheSame(oldItem: DisplayCoin, newItem: DisplayCoin): Boolean {
        return oldItem.coin.symbol == newItem.coin.symbol
    }

    override fun areContentsTheSame(oldItem: DisplayCoin, newItem: DisplayCoin): Boolean {
        return oldItem == newItem
    }
}
