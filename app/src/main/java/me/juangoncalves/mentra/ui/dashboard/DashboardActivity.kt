package me.juangoncalves.mentra.ui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.DashboardActivityBinding
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.ui.wallet_list.WalletListFragment

@AndroidEntryPoint
class DashboardActivity : FragmentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var binding: DashboardActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "NSV id: ${binding.fragmentContainer.id}")

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, WalletListFragment())
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
    }

}