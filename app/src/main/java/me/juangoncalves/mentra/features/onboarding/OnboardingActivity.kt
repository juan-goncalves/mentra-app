package me.juangoncalves.mentra.features.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.common.BundleKeys
import me.juangoncalves.mentra.databinding.OnboardingActivityBinding
import me.juangoncalves.mentra.extensions.empty
import me.juangoncalves.mentra.features.dashboard.DashboardActivity

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    private lateinit var binding: OnboardingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.currentStep.observe(this) { step ->
            binding.pager.setCurrentItem(step.index, true)
        }

        viewModel.closeOnboardingStream.observe(this) { notification ->
            notification.use { openDashboard() }
        }
    }

    private fun configureView() = with(binding) {
        pager.adapter = OnboardingStepsAdapter(this@OnboardingActivity)
        pager.offscreenPageLimit = 2
        binding.tabLayout.addOnTabSelectedListener(tabListener)
        TabLayoutMediator(tabLayout, pager) { _, _ ->
        }.attach()
    }

    private fun openDashboard() {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(BundleKeys.FirstRun, true)
        }
        startActivity(intent)
    }

    private val tabListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab == null) return
            viewModel.scrolledToStep(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) = empty()
        override fun onTabReselected(tab: TabLayout.Tab?) = empty()
    }

}
