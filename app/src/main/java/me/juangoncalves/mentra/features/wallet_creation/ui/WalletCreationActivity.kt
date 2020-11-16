package me.juangoncalves.mentra.features.wallet_creation.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.WalletCreationActivityBinding
import me.juangoncalves.mentra.extensions.addIfMissing
import me.juangoncalves.mentra.extensions.hideAllFragmentsIn
import me.juangoncalves.mentra.extensions.hideKeyboard
import me.juangoncalves.mentra.extensions.withFadeAnimation
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationViewModel
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationViewModel.Step

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
        viewModel.initialize()
        configureView()
        adjustBackButtonPosition()
        initObservers()
    }

    private fun configureView() {
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun initObservers() {
        viewModel.currentStepStream.observe(this) { nextStep ->
            when (nextStep) {
                Step.CoinSelection -> showFragment(
                    coinSelectionFragment,
                    CoinSelectionFragmentTag
                )
                Step.AmountInput -> showFragment(
                    amountInputFragment,
                    CoinAmountInputFragmentTag
                )
                Step.Done -> finish()
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
            .also { binding.root.hideKeyboard() }
    }

    /** Ensures that the back button isn't being drawn behind the status bar */
    private fun adjustBackButtonPosition() {
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            if (insets.systemWindowInsetTop == 0) return@setOnApplyWindowInsetsListener insets

            binding.backButton.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }

            insets
        }
    }

    override fun onBackPressed() {
        viewModel.backPressed()
    }

}
