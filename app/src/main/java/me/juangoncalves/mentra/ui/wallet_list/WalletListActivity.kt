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
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.extensions.hide
import me.juangoncalves.mentra.extensions.show
import me.juangoncalves.mentra.ui.add_wallet.WalletFormFragment

@AndroidEntryPoint
class WalletListActivity : AppCompatActivity() {

    private val viewModel: WalletListViewModel by viewModels()

    private lateinit var binding: WalletListActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewManager = LinearLayoutManager(this)
        binding.recyclerView.apply {
            layoutManager = viewManager
            adapter = WalletAdapter(emptyList())
        }
        binding.addWalletButton.setOnClickListener {
            WalletFormFragment().show(supportFragmentManager, "create_wallet")
        }

        viewModel.viewState.observe(this) { state ->
            when (state) {
                is WalletListViewModel.State.Loading -> binding.progressBar.show()
                is WalletListViewModel.State.Error -> {
                    binding.progressBar.hide()
                    Snackbar.make(binding.root, state.messageId, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { viewModel.retryWalletFetch() }
                        .show()
                }
                is WalletListViewModel.State.Loaded -> {
                    binding.progressBar.hide()
                    binding.portfolioValueTextView.text = state.portfolioValue.value.asCurrency(
                        symbol = "$",
                        forcedDecimalPlaces = 2
                    )
                    binding.recyclerView.adapter = WalletAdapter(state.wallets)
                }
            }
        }
    }
}
