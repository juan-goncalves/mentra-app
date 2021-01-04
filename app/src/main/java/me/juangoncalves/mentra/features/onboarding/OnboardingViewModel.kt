package me.juangoncalves.mentra.features.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.usecases.currency.GetSupportedCurrencies
import java.util.*

class OnboardingViewModel @ViewModelInject constructor(
    private val getSupportedCurrencies: GetSupportedCurrencies
) : ViewModel() {

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

    private val _currentStep: MutableLiveData<Step> = MutableLiveData(Step.Benefits)
    val currentStep: LiveData<Step> = _currentStep

    private val _currenciesStream: MutableLiveData<List<Currency>> = MutableLiveData(emptyList())
    val currenciesStream: MutableLiveData<List<Currency>> = _currenciesStream

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() = viewModelScope.launch(Dispatchers.Default) {
        val currencies = getSupportedCurrencies().rightValue ?: emptySet()
        val sorted = currencies.toList().sortedBy { it.displayName }.reversed()
        _currenciesStream.postValue(sorted)
    }

    fun scrolledToStep(index: Int) {
        val step = Step.from(index)
        _currentStep.value = step
    }

    fun startSelected() {
        _currentStep.value = Step.ConfigureAutoRefresh
    }

}