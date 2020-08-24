package me.juangoncalves.mentra.ui.wallet_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.WalletListItemBinding
import me.juangoncalves.mentra.extensions.asCoinAmount
import me.juangoncalves.mentra.extensions.asCurrency

class WalletAdapter(data: List<DisplayWallet>) : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    var data: List<DisplayWallet> = data
        set(value) {
            val diffResult = DiffUtil.calculateDiff(WalletDiffCallback(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WalletListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayWallet = data[position]
        holder.binding.apply {
            coinNameTextView.text = displayWallet.wallet.coin.name
            coinAmountTextView.text = displayWallet.wallet.amount.asCoinAmount()
            coinPriceTextView.text = displayWallet.currentCoinPrice.asCurrency(symbol = "$")
            walletValueTextView.text = displayWallet.currentWalletPrice.asCurrency(symbol = "$")

            Glide.with(root)
                .load(displayWallet.gradientIconUrl)
                // TODO: Make a placeholder png with the same dimensions as the gradient drawables
                // .placeholder(R.drawable.coin_placeholder)
                // .transition(DrawableTransitionOptions.withCrossFade())
                .error(
                    Glide.with(root)
                        .load(displayWallet.wallet.coin.imageUrl)
                        .circleCrop()
                        .error(R.drawable.coin_placeholder)
                )
                .into(logoImageView)
        }
    }

    inner class ViewHolder(val binding: WalletListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class WalletDiffCallback(
    private val old: List<DisplayWallet>,
    private val next: List<DisplayWallet>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = next.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].wallet.id == next[newItemPosition].wallet.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].wallet == next[newItemPosition].wallet
    }

}