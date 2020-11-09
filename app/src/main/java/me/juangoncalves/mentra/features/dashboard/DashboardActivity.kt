package me.juangoncalves.mentra.features.dashboard

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.DashboardActivityBinding
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.extensions.asPercentage
import me.juangoncalves.mentra.features.stats.StatsFragment
import me.juangoncalves.mentra.features.wallet_list.ui.WalletListFragment
import kotlin.math.absoluteValue
import kotlin.math.sign

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
        adjustHeaderPosition()

        supportFragmentManager.beginTransaction()
            .addIfMissing(statsFragment, StatsFragmentTag)
            .addIfMissing(walletListFragment, WalletsFragmentTag)
            .commit()

        initObservers()
    }

    private fun initObservers() {
        viewModel.portfolioValue.observe(this) { price ->
            binding.portfolioValueTextView.text = price.value.asCurrency(
                symbol = "$",
                forcedDecimalPlaces = 2
            )
        }

        viewModel.lastDayValueChange.observe(this) { (amountDiff, percentChange) ->
            val (sign, icon, color) = getIconParametersForSign(amountDiff)

            binding.portfolioChangeTextView.text = getString(
                R.string.last_day_change,
                sign,
                amountDiff.value.absoluteValue.asCurrency(symbol = "$", forcedDecimalPlaces = 2),
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
            amountDiff.value.sign > 0 -> Triple(
                "+",
                AppCompatResources.getDrawable(this, R.drawable.ic_arrow_up_20),
                getColor(R.color.screaming_green)
            )
            amountDiff.value.sign < 0 -> Triple(
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
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.fade_in, R.animator.fade_out,
                R.animator.fade_in, R.animator.fade_out
            )
            .hide(walletListFragment)
            .show(statsFragment)
            .commit()

        val walletIcon = AppCompatResources.getDrawable(this, R.drawable.ic_wallet)
        binding.navButton.setImageDrawable(walletIcon)
        binding.navButton.setOnClickListener { viewModel.openWalletsSelected() }
    }

    private fun loadWalletsTab() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.fade_in, R.animator.fade_out,
                R.animator.fade_in, R.animator.fade_out
            )
            .hide(statsFragment)
            .show(walletListFragment)
            .commit()

        val chartIcon = AppCompatResources.getDrawable(this, R.drawable.ic_chart)
        binding.navButton.setImageDrawable(chartIcon)
        binding.navButton.setOnClickListener { viewModel.openStatsSelected() }
    }

    private fun FragmentTransaction.addIfMissing(
        fragment: Fragment,
        tag: String
    ): FragmentTransaction = apply {
        if (!fragment.isAdded) add(binding.fragmentContainer.id, fragment, tag)
    }

    override fun onBackPressed() {
        viewModel.backPressed()
    }

    /** Ensures that the header contents aren't being drawn behind the status bar */
    private fun adjustHeaderPosition() {
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            if (insets.systemWindowInsetTop == 0) return@setOnApplyWindowInsetsListener insets

            val expandedHeaderHeight =
                resources.getDimensionPixelSize(R.dimen.expanded_header_height)

            val collapsedHeaderHeight =
                resources.getDimensionPixelSize(R.dimen.collapsed_header_height)

            binding.root.getConstraintSet(R.id.expanded)?.run {
                setMargin(R.id.settingsButton, ConstraintSet.TOP, insets.systemWindowInsetTop)
                constrainHeight(
                    R.id.headerBackground,
                    expandedHeaderHeight + insets.systemWindowInsetTop
                )
            }

            binding.root.getConstraintSet(R.id.collapsed)?.run {
                setMargin(R.id.settingsButton, ConstraintSet.TOP, insets.systemWindowInsetTop)
                setMargin(
                    R.id.portfolioValueTextView,
                    ConstraintSet.TOP,
                    insets.systemWindowInsetTop
                )
                constrainHeight(
                    R.id.headerBackground,
                    collapsedHeaderHeight + insets.systemWindowInsetTop
                )
            }

            insets
        }
    }

}