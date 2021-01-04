package me.juangoncalves.mentra.features.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import me.juangoncalves.mentra.databinding.OnboardingActivityBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: OnboardingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OnboardingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
    }

    private fun configureView() = with(binding) {
        pager.adapter = OnboardingStepsAdapter(this@OnboardingActivity)
        TabLayoutMediator(tabLayout, pager) { _, _ ->
        }.attach()
    }

}
