package me.juangoncalves.mentra.features.wallet_list.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.WalletListItemBinding
import me.juangoncalves.mentra.extensions.asCoinAmount
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import java.util.*

class WalletAdapter : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    var data: List<WalletListViewState.Wallet>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ: AsyncListDiffer<WalletListViewState.Wallet> =
        AsyncListDiffer(this, WalletItemCallback())

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

            ImageRequest.Builder(root.context)
                .diskCachePolicy(CachePolicy.ENABLED)
                .placeholder(R.drawable.coin_placeholder)
                .data(Uri.parse(wallet.iconUrl))
                .target(logoImageView)
                .transformations(CircleCropTransformation())
                .crossfade(root.resources.getInteger(android.R.integer.config_shortAnimTime))
                .build()
                .also { request -> Coil.enqueue(request) }
        }
    }

    inner class ViewHolder(val binding: WalletListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun WalletListViewState.Price.asCurrencyAmount(
        forcedDecimalPlaces: Int? = null,
        absolute: Boolean = false
    ): String {
        val symbol = currency.getSymbol(Locale.getDefault())

        if (isPlaceholder) return "$symbol ---,---.--"

        return value
            .let { if (absolute) it.abs() else it }
            .asCurrency(symbol, forcedDecimalPlaces)
    }
}


class WalletItemCallback : DiffUtil.ItemCallback<WalletListViewState.Wallet>() {
    override fun areItemsTheSame(
        oldItem: WalletListViewState.Wallet,
        newItem: WalletListViewState.Wallet
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: WalletListViewState.Wallet,
        newItem: WalletListViewState.Wallet
    ): Boolean {
        return oldItem == newItem
    }
}