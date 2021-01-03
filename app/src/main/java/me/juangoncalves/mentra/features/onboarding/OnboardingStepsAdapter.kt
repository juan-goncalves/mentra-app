package me.juangoncalves.mentra.features.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingStepsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OnboardingBenefitsFragment.newInstance()
        1 -> OnboardingBenefitsFragment.newInstance()
        else -> error("Unsupported onboarding step: $position")
    }

}