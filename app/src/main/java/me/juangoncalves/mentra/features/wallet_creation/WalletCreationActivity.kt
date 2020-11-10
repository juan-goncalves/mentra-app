package me.juangoncalves.mentra.features.wallet_creation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.WalletCreationActivityBinding

@AndroidEntryPoint
class WalletCreationActivity : FragmentActivity() {

    private val viewModel: WalletCreationViewModel by viewModels()

    private lateinit var binding: WalletCreationActivityBinding

    private val coinSelectionFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(CoinSelectionFragmentTag)
            ?: CoinSelectionFragment()

    companion object {
        const val CoinSelectionFragmentTag = "coin_selection_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletCreationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
        initObservers()
    }

    private fun configureView() {
        supportFragmentManager.beginTransaction()
            .addIfMissing(coinSelectionFragment, CoinSelectionFragmentTag)
            .show(coinSelectionFragment)
            .commit()

//        binding.saveButton.setOnClickListener {
//            val amountStr = binding.amountInput.text.toString()
//            viewModel.submitForm(coinAdapter.selectedCoin, amountStr)
//        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.warning.observe(this) { event ->
            event.use { messageId ->
                Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.lighting_yellow))
                    .show()
            }
        }

        viewModel.onSuccessfulSave.observe(this) {
            finish()
        }
    }

    private fun FragmentTransaction.addIfMissing(
        fragment: Fragment,
        tag: String
    ): FragmentTransaction = apply {
        if (!fragment.isAdded) add(binding.fragmentContainer.id, fragment, tag)
    }

}
