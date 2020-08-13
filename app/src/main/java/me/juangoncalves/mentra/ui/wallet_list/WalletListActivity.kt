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

        viewModel.viewState.observe(this) { state ->
            when (state) {
                is WalletListViewModel.State.Loading -> {
                    // TODO: Show spinner
                }
                is WalletListViewModel.State.Error -> {
                    Snackbar.make(binding.root, state.messageId, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { viewModel.retryWalletFetch() }
                        .show()
                }
                is WalletListViewModel.State.Loaded -> {
                    binding.recyclerView.adapter = WalletAdapter(state.wallets)
                }
            }
        }
    }
}
