package me.juangoncalves.mentra.features.wallet_list.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.WalletListItemBinding
import me.juangoncalves.mentra.extensions.asCoinAmount
import me.juangoncalves.mentra.extensions.asCurrencyAmount
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState

class WalletAdapter : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    var data: List<WalletListViewState.Wallet>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ: AsyncListDiffer<WalletListViewState.Wallet> =
        AsyncListDiffer(this, WalletItemCallback())

    private val crossFadeFactory = DrawableCrossFadeFactory.Builder()
        .setCrossFadeEnabled(true)
        .build()

    override fun getItemCount() = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WalletListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wallet = differ.currentList[position]
        holder.binding.apply {
            coinNameTextView.text = wallet.coin.name
            coinAmountTextView.text = wallet.amountOfCoin.asCoinAmount()
            coinPriceTextView.text = wallet.coin.value.asCurrencyAmount()
            walletValueTextView.text = wallet.value.asCurrencyAmount()

            Glide.with(root)
                .load(wallet.iconUrl)
                .placeholder(R.drawable.coin_placeholder)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade(crossFadeFactory))
                .into(logoImageView)
        }
    }

    inner class ViewHolder(val binding: WalletListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}


class WalletItemCallback : DiffUtil.ItemCallback<WalletListViewState.Wallet>() {
    override fun areItemsTheSame(
        oldItem: WalletListViewState.Wallet,
        newItem: WalletListViewState.Wallet
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: WalletListViewState.Wallet,
        newItem: WalletListViewState.Wallet
    ): Boolean {
        return oldItem == newItem
    }
}