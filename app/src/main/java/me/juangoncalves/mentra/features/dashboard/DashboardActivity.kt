package me.juangoncalves.mentra.features.dashboard

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.DashboardActivityBinding
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.extensions.*
import me.juangoncalves.mentra.features.settings.ui.SettingsActivity
import me.juangoncalves.mentra.features.stats.ui.StatsFragment
import me.juangoncalves.mentra.features.wallet_list.ui.WalletListFragment

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DashboardActivity : FragmentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var binding: DashboardActivityBinding

    private val statsFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(StatsFragmentTag) ?: StatsFragment()

    private val walletListFragment: Fragment
        get() = supportFragmentManager.findFragmentByTag(WalletsFragmentTag) ?: WalletListFragment()

    companion object {
        const val WalletsFragmentTag = "wallets_fragment"
        const val StatsFragmentTag = "stats_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
        initObservers()
    }

    private fun configureView() {
        supportFragmentManager.beginTransaction()
            .addIfMissing(binding.fragmentContainer, statsFragment, StatsFragmentTag)
            .addIfMissing(binding.fragmentContainer, walletListFragment, WalletsFragmentTag)
            .commit()

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.root.onTransitionCompleted { id ->
            binding.navButton.isClickable = id == R.id.expanded
        }
    }

    private fun initObservers() {
        viewModel.portfolioValue.observe(this) { price ->
            binding.portfolioValueTextView.text = price.asCurrencyAmount(forcedDecimalPlaces = 2)
        }

        viewModel.lastDayValueChange.observe(this) { (amountDiff, percentChange) ->
            val (sign, icon, color) = getIconParametersForSign(amountDiff)

            binding.portfolioChangeTextView.text = getString(
                R.string.last_day_change,
                sign,
                amountDiff.asCurrencyAmount(absolute = true, forcedDecimalPlaces = 2),
                percentChange.asPercentage()
            )

            binding.portfolioChangeImageView.setImageDrawable(icon)
            binding.portfolioChangeImageView.colorFilter =
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        viewModel.openedTab.observe(this) { screen ->
            when (screen) {
                DashboardViewModel.Tab.STATS -> loadStatsTab()
                DashboardViewModel.Tab.WALLETS -> loadWalletsTab()
            }
        }

        viewModel.closeEvent.observe(this) { event ->
            event.use { finish() }
        }
    }

    private fun getIconParametersForSign(amountDiff: Price): Triple<String, Drawable?, Int> {
        return when {
            amountDiff.value.signum() > 0 -> Triple(
                "+",
                AppCompatResources.getDrawable(this, R.drawable.ic_arrow_up_20),
                getColor(R.color.screaming_green)
            )
            amountDiff.value.signum() < 0 -> Triple(
                "-",
                AppCompatResources.getDrawable(this, R.drawable.ic_arrow_down_20),
                getColor(R.color.tobasco)
            )
            else -> Triple(
                "",
                AppCompatResources.getDrawable(this, R.drawable.ic_coins),
                getColor(android.R.color.transparent)
            )
        }
    }

    private fun loadStatsTab() {
        showFragment(statsFragment)
        val walletIcon = AppCompatResources.getDrawable(this, R.drawable.ic_wallet)
        binding.navButton.setImageDrawable(walletIcon)
        binding.navButton.setOnClickListener { viewModel.openWalletsSelected() }
    }

    private fun loadWalletsTab() {
        showFragment(walletListFragment)
        val chartIcon = AppCompatResources.getDrawable(this, R.drawable.ic_chart)
        binding.navButton.setImageDrawable(chartIcon)
        binding.navButton.setOnClickListener { viewModel.openStatsSelected() }
    }

    override fun onBackPressed() {
        viewModel.backPressed()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .hideAllFragmentsIn(supportFragmentManager)
            .withFadeAnimation()
            .show(fragment)
            .commit()
    }

}