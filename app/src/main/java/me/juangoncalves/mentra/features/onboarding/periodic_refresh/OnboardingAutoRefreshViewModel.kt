package me.juangoncalves.mentra.features.onboarding.periodic_refresh

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.usecases.preference.UpdatePeriodicRefreshPreference
import me.juangoncalves.mentra.failures.FailurePublisher
import me.juangoncalves.mentra.failures.GeneralFailurePublisher
import java.time.Duration

class OnboardingAutoRefreshViewModel @ViewModelInject constructor(
    private val updatePeriodicRefreshPreference: UpdatePeriodicRefreshPreference
) : ViewModel(), FailurePublisher by GeneralFailurePublisher() {

    fun periodSelected(duration: Duration) = viewModelScope.launch {
        updatePeriodicRefreshPreference.runHandlingFailure(duration)
    }

}
