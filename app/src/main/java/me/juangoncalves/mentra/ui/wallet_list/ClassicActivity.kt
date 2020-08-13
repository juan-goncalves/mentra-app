package me.juangoncalves.mentra.ui.wallet_list

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.ActivityClassicBinding
import me.juangoncalves.mentra.extensions.TAG

@AndroidEntryPoint
class ClassicActivity : AppCompatActivity() {

    private val viewModel: WalletListViewModel by viewModels()

    private lateinit var binding: ActivityClassicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val viewManager = LinearLayoutManager(this)
        binding.recyclerView.apply {
            layoutManager = viewManager
            adapter = WalletAdapter(emptyList())
        }

        viewModel.viewState.observe(this) { state ->
            Log.d(TAG, "Received state: $state")
            when (state) {
                is WalletListViewModel.State.Loading -> {
                }
                is WalletListViewModel.State.Error -> {
                }
                is WalletListViewModel.State.Loaded -> {
                    binding.recyclerView.adapter = WalletAdapter(state.wallets)
                }
            }
        }
    }
}
