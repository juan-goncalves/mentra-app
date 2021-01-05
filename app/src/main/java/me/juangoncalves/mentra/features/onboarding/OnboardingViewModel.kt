package me.juangoncalves.mentra.features.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.common.Notification
import me.juangoncalves.mentra.domain_layer.usecases.preference.FinishOnboarding

class OnboardingViewModel @ViewModelInject constructor(
    private val finishOnboarding: FinishOnboarding
) : ViewModel() {

    private val _currentStep: MutableLiveData<Step> = MutableLiveData(Step.Benefits)
    val currentStep: LiveData<Step> = _currentStep

    private val _closeOnboardingStream: MutableLiveData<Notification> = MutableLiveData()
    val closeOnboardingStream: LiveData<Notification> = _closeOnboardingStream

    fun scrolledToStep(index: Int) {
        val step = Step.from(index)
        _currentStep.value = step
    }

    fun startSelected() {
        _currentStep.value = Step.ConfigureAutoRefresh
    }

    fun finishSelected() = viewModelScope.launch {
        finishOnboarding()
        _closeOnboardingStream.value = Notification()
    }

    sealed class Step(val index: Int) {
        object Benefits : Step(0)
        object ConfigureAutoRefresh : Step(1)
        object ConfigureCurrency : Step(2)
        object Finished : Step(3)

        companion object {
            fun from(index: Int): Step = when (index) {
                0 -> Benefits
                1 -> ConfigureAutoRefresh
                2 -> ConfigureCurrency
                3 -> Finished
                else -> error("Invalid step: $index")
            }
        }
    }

}