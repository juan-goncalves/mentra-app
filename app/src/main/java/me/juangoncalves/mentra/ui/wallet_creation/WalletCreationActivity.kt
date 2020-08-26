package me.juangoncalves.mentra.ui.wallet_creation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.WalletCreationActivityBinding

@AndroidEntryPoint
class WalletCreationActivity : AppCompatActivity() {

    private val viewModel: WalletCreationViewModel by viewModels()
    private val coinAdapter: CoinAdapter = CoinAdapter(emptyList())

    private lateinit var binding: WalletCreationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletCreationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
        initObservers()
    }

    private fun configureView() {
        val viewManager = LinearLayoutManager(this)
        binding.coinSelectionList.apply {
            layoutManager = viewManager
            adapter = coinAdapter
            setHasFixedSize(true)
        }
        binding.coinNameInput.addTextChangedListener { text ->
            viewModel.filterByName(text.toString())
        }
        binding.saveButton.setOnClickListener {
            val amount = binding.amountInput.text.toString().toDouble()
            coinAdapter.selectedCoin?.let { coin ->
                viewModel.submitForm(coin, amount)
            }
        }
    }

    private fun initObservers() {
        viewModel.coins.observe(this) { coins ->
            coinAdapter.data = coins
        }
        viewModel.shouldScrollToStart.observe(this) {
            binding.coinSelectionList.smoothScrollToPosition(0)
        }
    }

}
