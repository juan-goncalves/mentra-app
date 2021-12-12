package me.juangoncalves.mentra.features.wallet_creation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.CoinRecommendationItemBinding
import me.juangoncalves.mentra.domain_layer.models.Coin


class CoinAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<CoinAdapter.ViewHolder>() {

    interface Listener {
        fun onCoinSelected(coin: Coin)
        fun onCommitCoinListUpdates()
    }

    var data: List<Coin>
        get() = differ.currentList
        set(value) = differ.submitList(value, listener::onCommitCoinListUpdates)

    private val differ: AsyncListDiffer<Coin> = AsyncListDiffer(this, CoinDiffItemCallback())

    override fun getItemCount() = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CoinRecommendationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, CoinClickListener())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coin = differ.currentList[position]
        holder.coinClickListener.coin = coin
        holder.binding.apply {
            nameTextView.text = coin.name

            ImageRequest.Builder(root.context)
                .diskCachePolicy(CachePolicy.ENABLED)
                .placeholder(R.drawable.coin_placeholder)
                .data(coin.imageUrl)
                .target(coinImageView)
                .transformations(CircleCropTransformation())
                .crossfade(root.resources.getInteger(android.R.integer.config_shortAnimTime))
                .build()
                .also { request -> Coil.enqueue(request) }
        }
    }

    inner class ViewHolder(
        val binding: CoinRecommendationItemBinding,
        val coinClickListener: CoinClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener(coinClickListener)
        }
    }

    inner class CoinClickListener : View.OnClickListener {

        var coin: Coin? = null

        override fun onClick(v: View?) {
            coin?.run { listener.onCoinSelected(this) }
        }
    }
}

class CoinDiffItemCallback : DiffUtil.ItemCallback<Coin>() {
    override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem == newItem
    }
}
