package me.juangoncalves.mentra.features.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.juangoncalves.mentra.features.onboarding.benefits.OnboardingBenefitsFragment
import me.juangoncalves.mentra.features.onboarding.currency.OnboardingCurrencyFragment
import me.juangoncalves.mentra.features.onboarding.finished.OnboardingFinishedFragment
import me.juangoncalves.mentra.features.onboarding.periodic_refresh.OnboardingAutoRefreshFragment

class OnboardingStepsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        lazy { OnboardingBenefitsFragment.newInstance() },
        lazy { OnboardingAutoRefreshFragment.newInstance(1) },
        lazy { OnboardingCurrencyFragment.newInstance(2) },
        lazy { OnboardingFinishedFragment.newInstance(3) }
    )

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = fragments[position].value

}
