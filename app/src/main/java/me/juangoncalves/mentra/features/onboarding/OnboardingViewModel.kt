package me.juangoncalves.mentra.features.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel @ViewModelInject constructor() : ViewModel() {

    private val _currentStep: MutableLiveData<Step> = MutableLiveData(Step.Benefits)
    val currentStep: LiveData<Step> = _currentStep

    fun scrolledToStep(index: Int) {
        val step = Step.from(index)
        _currentStep.value = step
    }

    fun startSelected() {
        _currentStep.value = Step.ConfigureAutoRefresh
    }

    sealed class Step(val index: Int) {
        object Benefits : Step(0)
        object ConfigureAutoRefresh : Step(1)
        object ConfigureCurrency : Step(2)

        companion object {
            fun from(index: Int): Step = when (index) {
                0 -> Benefits
                1 -> ConfigureAutoRefresh
                2 -> ConfigureCurrency
                else -> error("Invalid step: $index")
            }
        }
    }

}