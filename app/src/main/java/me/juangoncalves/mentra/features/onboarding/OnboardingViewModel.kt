package me.juangoncalves.mentra.features.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel @ViewModelInject constructor() : ViewModel() {

    sealed class Step(val index: Int) {
        object Benefits : Step(0)
        object ConfigureAutoRefresh : Step(1)

        companion object {
            fun from(index: Int): Step = when (index) {
                0 -> Benefits
                1 -> ConfigureAutoRefresh
                else -> error("Invalid step: $index")
            }
        }
    }

    val currentStep: MutableLiveData<Step> = MutableLiveData(Step.Benefits)

    fun scrolledToStep(index: Int) {
        currentStep.value = Step.from(index)
    }

    fun startSelected() {
        currentStep.value = Step.ConfigureAutoRefresh
    }

}