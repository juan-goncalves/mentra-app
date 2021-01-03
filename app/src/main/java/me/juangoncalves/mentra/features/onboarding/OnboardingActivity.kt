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
        binding.pager.adapter = OnboardingStepsAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = "Step ${(position + 1)}"
        }.attach()
    }

}
