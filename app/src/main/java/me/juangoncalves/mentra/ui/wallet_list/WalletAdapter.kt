package me.juangoncalves.mentra.ui.wallet_list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.juangoncalves.mentra.databinding.WalletListItemBinding
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.extensions.asCoinAmount
import me.juangoncalves.mentra.extensions.asCurrency

class WalletAdapter(val data: List<DisplayWallet>) : RecyclerView.Adapter<WalletViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val binding =
            WalletListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val displayWallet = data[position]
        Log.d(TAG, "Binding $displayWallet | ${holder.binding}")
        holder.binding.apply {
            coinNameTextView.text = displayWallet.wallet.coin.name
            coinAmountTextView.text = displayWallet.wallet.amount.asCoinAmount()
            coinPriceTextView.text = displayWallet.currentCoinPrice.asCurrency(symbol = "$")
            walletValueTextView.text = displayWallet.currentWalletPrice.asCurrency(symbol = "$")

            Glide.with(root)
                .load(displayWallet.wallet.coin.imageUrl)
                .into(logoImageView)
        }
    }

    override fun getItemCount() = data.size
}

class WalletViewHolder(val binding: WalletListItemBinding) : RecyclerView.ViewHolder(binding.root)
