package me.juangoncalves.mentra.ui.wallet_list

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.WalletListActivityBinding
import me.juangoncalves.mentra.extensions.animateVisibility
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.ui.add_wallet.WalletFormFragment

@AndroidEntryPoint
class WalletListActivity : AppCompatActivity() {

    private val viewModel: WalletListViewModel by viewModels()

    private lateinit var binding: WalletListActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
        initObservers()
    }

    private fun configureView() {
        val viewManager = LinearLayoutManager(this)
        binding.recyclerView.apply {
            layoutManager = viewManager
            adapter = WalletAdapter(emptyList())
        }

        binding.addWalletButton.setOnClickListener {
            WalletFormFragment().show(supportFragmentManager, "create_wallet")
        }
    }

    private fun initObservers() {
        viewModel.shouldShowProgressBar.observe(this) { shouldShow ->
            binding.progressBar.animateVisibility(shouldShow)
        }

        viewModel.portfolioValue.observe(this) { price ->
            binding.portfolioValueTextView.text = price.value.asCurrency(
                symbol = "$",
                forcedDecimalPlaces = 2
            )
        }

        viewModel.wallets.observe(this) { wallets ->
            binding.recyclerView.adapter = WalletAdapter(wallets)
        }

        viewModel.error.observe(this) { error ->
            Snackbar.make(binding.root, error.messageId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { error.retryAction() }
                .show()
        }
    }
}
