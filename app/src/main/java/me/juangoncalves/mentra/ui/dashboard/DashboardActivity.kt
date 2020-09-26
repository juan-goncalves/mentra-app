package me.juangoncalves.mentra.ui.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.DashboardActivityBinding
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.extensions.hide
import me.juangoncalves.mentra.extensions.showExistingOrCreate
import me.juangoncalves.mentra.ui.stats.StatsFragment
import me.juangoncalves.mentra.ui.wallet_list.WalletListFragment

@AndroidEntryPoint
class DashboardActivity : FragmentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var binding: DashboardActivityBinding

    companion object {
        const val WALLETS_FRAGMENT_TAG = "wallets_fragment"
        const val STATS_FRAGMENT_TAG = "stats_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()
    }

    private fun initObservers() {
        viewModel.portfolioValue.observe(this) { price ->
            binding.portfolioValueTextView.text = price.value.asCurrency(
                symbol = "$",
                forcedDecimalPlaces = 2
            )
        }

        viewModel.openedTab.observe(this) { screen ->
            when (screen) {
                DashboardViewModel.Tab.STATS -> loadStatsTab()
                DashboardViewModel.Tab.WALLETS -> loadWalletsTab()
            }
        }

        viewModel.closeEvent.observe(this) { event ->
            event.content?.run { finish() }
        }
    }

    private fun loadStatsTab() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
            .showExistingOrCreate(
                STATS_FRAGMENT_TAG,
                lazy { StatsFragment() },
                supportFragmentManager
            )
            .hide(WALLETS_FRAGMENT_TAG, supportFragmentManager)
            .commit()

        binding.navButton.setImageDrawable(getDrawable(R.drawable.ic_wallet))
        binding.navButton.setOnClickListener { viewModel.openWalletsSelected() }
    }

    private fun loadWalletsTab() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
            .showExistingOrCreate(
                WALLETS_FRAGMENT_TAG,
                lazy { WalletListFragment() },
                supportFragmentManager
            )
            .hide(STATS_FRAGMENT_TAG, supportFragmentManager)
            .commit()

        binding.navButton.setImageDrawable(getDrawable(R.drawable.ic_chart))
        binding.navButton.setOnClickListener { viewModel.openStatsSelected() }
    }

    override fun onBackPressed() {
        viewModel.backPressed()
    }

}