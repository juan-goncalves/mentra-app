package me.juangoncalves.mentra.features.onboarding

import android.os.Parcelable
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import me.juangoncalves.mentra.common.Notification
import me.juangoncalves.mentra.domain_layer.usecases.preference.FinishOnboarding
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val finishOnboarding: FinishOnboarding,
    handle: SavedStateHandle
) : ViewModel() {

    private object Keys {
        const val Step = "step_handle_key"
    }

    private val _currentStep: MutableLiveData<Step> = handle.getLiveData(Keys.Step, Step.Benefits)
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


    sealed class Step(val index: Int) : Parcelable {
        @Parcelize object Benefits : Step(0)
        @Parcelize object ConfigureAutoRefresh : Step(1)
        @Parcelize object ConfigureCurrency : Step(2)
        @Parcelize object Finished : Step(3)

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