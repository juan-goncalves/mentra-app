package me.juangoncalves.mentra.features.onboarding.periodic_refresh

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetPeriodicRefreshOptions
import me.juangoncalves.mentra.domain_layer.usecases.preference.UpdatePeriodicRefreshPreference
import me.juangoncalves.mentra.failures.FailurePublisher
import me.juangoncalves.mentra.failures.GeneralFailurePublisher
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class OnboardingAutoRefreshViewModel @Inject constructor(
    private val getPeriodicRefreshOptions: GetPeriodicRefreshOptions,
    private val updatePeriodicRefreshPreference: UpdatePeriodicRefreshPreference
) : ViewModel(), FailurePublisher by GeneralFailurePublisher() {

    private val _durationsStream: MutableLiveData<List<Duration>> = MutableLiveData(emptyList())
    val durationsStream: LiveData<List<Duration>> = _durationsStream

    init {
        viewModelScope.launch {
            getPeriodicRefreshOptions.runHandlingFailure(
                params = Unit,
                onSuccess = { durations -> _durationsStream.value = durations }
            )
        }
    }

    fun periodSelected(duration: Duration) = viewModelScope.launch {
        updatePeriodicRefreshPreference.runHandlingFailure(duration)
    }

}
