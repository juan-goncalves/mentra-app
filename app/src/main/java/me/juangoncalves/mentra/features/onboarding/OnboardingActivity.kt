package me.juangoncalves.mentra.features.onboarding

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import me.juangoncalves.mentra.databinding.OnboardingActivityBinding
import me.juangoncalves.mentra.extensions.empty

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
    }

    private fun configureView() = with(binding) {
        pager.adapter = OnboardingStepsAdapter(this@OnboardingActivity)
        binding.tabLayout.addOnTabSelectedListener(tabListener)
        TabLayoutMediator(tabLayout, pager) { _, _ ->
        }.attach()
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
