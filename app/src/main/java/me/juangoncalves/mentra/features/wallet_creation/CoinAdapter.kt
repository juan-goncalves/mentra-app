package me.juangoncalves.mentra.features.wallet_creation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.CoinRecommendationItemBinding
import me.juangoncalves.mentra.domain.models.Coin

class CoinAdapter(
    private val commitCallback: () -> Unit
) : RecyclerView.Adapter<CoinAdapter.ViewHolder>() {

    var data: List<Coin>
        get() = differ.currentList
        set(value) = differ.submitList(value, commitCallback)


    private val differ: AsyncListDiffer<Coin> = AsyncListDiffer(this, CoinItemCallback())

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
        val coin = differ.currentList[position];
        holder.binding.apply {
            nameTextView.text = coin.name

            Glide.with(root)
                .load(coin.imageUrl)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.coin_placeholder)
                .into(coinImageView)
        }
    }

    inner class ViewHolder(val binding: CoinRecommendationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class CoinItemCallback : DiffUtil.ItemCallback<Coin>() {
    override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem == newItem
    }
}
