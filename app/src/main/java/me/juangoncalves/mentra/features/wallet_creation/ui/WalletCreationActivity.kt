package me.juangoncalves.mentra.features.wallet_creation.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.WalletCreationActivityBinding
import me.juangoncalves.mentra.extensions.addIfMissing
import me.juangoncalves.mentra.extensions.hideAllFragmentsIn
import me.juangoncalves.mentra.extensions.withFadeAnimation
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationState
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationViewModel

@AndroidEntryPoint
class WalletCreationActivity : FragmentActivity() {

    private val viewModel: WalletCreationViewModel by viewModels()

    private lateinit var binding: WalletCreationActivityBinding

    private val coinSelectionFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(CoinSelectionFragmentTag)
            ?: CoinSelectionFragment()

    private val amountInputFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(CoinAmountInputFragmentTag)
            ?: CoinAmountInputFragment()

    companion object {
        const val CoinSelectionFragmentTag = "coin_selection_fragment"
        const val CoinAmountInputFragmentTag = "coin_amount_input_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletCreationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
        initObservers()
    }

    private fun configureView() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.viewStateStream.observe(this) { state ->
            when (state.currentStep) {
                WalletCreationState.Step.CoinSelection -> showFragment(
                    coinSelectionFragment,
                    CoinSelectionFragmentTag
                )
                WalletCreationState.Step.AmountInput -> showFragment(
                    amountInputFragment,
                    CoinAmountInputFragmentTag
                )
                WalletCreationState.Step.Done -> finish()
            }
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .addIfMissing(binding.fragmentContainer, fragment, tag)
            .hideAllFragmentsIn(supportFragmentManager)
            .withFadeAnimation()
            .show(fragment)
            .commit()
    }

    override fun onBackPressed() {
        viewModel.backPressed()
    }

}
