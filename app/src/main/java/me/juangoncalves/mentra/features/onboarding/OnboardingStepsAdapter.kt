package me.juangoncalves.mentra.features.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.juangoncalves.mentra.features.onboarding.currency.OnboardingCurrencyFragment

class OnboardingStepsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OnboardingBenefitsFragment.newInstance()
        1 -> OnboardingAutoRefreshFragment.newInstance(position)
        2 -> OnboardingCurrencyFragment.newInstance(position)
        else -> error("Unsupported onboarding step: $position")
    }

}