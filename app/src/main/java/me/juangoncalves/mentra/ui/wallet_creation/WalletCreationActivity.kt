package me.juangoncalves.mentra.ui.wallet_creation

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.WalletCreationActivityBinding

@AndroidEntryPoint
class WalletCreationActivity : AppCompatActivity() {

    private val viewModel: WalletCreationViewModel by viewModels()
    private val coinAdapter: CoinAdapter = CoinAdapter()

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
            viewModel.submitQuery(text.toString())
        }

        binding.saveButton.setOnClickListener {
            val amountStr = binding.amountInput.text.toString()
            viewModel.submitForm(coinAdapter.selectedCoin, amountStr)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.coins.observe(this) { coins ->
            coinAdapter.submitList(coins)
        }

        viewModel.shouldScrollToStart.observe(this) {
            binding.coinSelectionList.smoothScrollToPosition(0)
        }

        viewModel.warning.observe(this) { event ->
            event.getContent()?.let { messageId ->
                Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.lighting_yellow))
                    .show()
            }
        }

        // TODO: Refactor to VM to VM communication
        viewModel.onSuccessfulSave.observe(this) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

}