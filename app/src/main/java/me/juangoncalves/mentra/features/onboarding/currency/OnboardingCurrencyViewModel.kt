package me.juangoncalves.mentra.features.onboarding.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.extensions.isLeft
import me.juangoncalves.mentra.domain_layer.extensions.orUSD
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.usecases.currency.GetSupportedCurrencies
import me.juangoncalves.mentra.domain_layer.usecases.preference.UpdateCurrencyPreference
import me.juangoncalves.mentra.failures.FailurePublisher
import me.juangoncalves.mentra.failures.GeneralFailurePublisher
import me.juangoncalves.mentra.platform.locale.LocaleProvider
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OnboardingCurrencyViewModel @Inject constructor(
    private val getSupportedCurrencies: GetSupportedCurrencies,
    private val updateCurrencyPreference: UpdateCurrencyPreference,
    private val localeProvider: LocaleProvider,
) : ViewModel(), FailurePublisher by GeneralFailurePublisher() {

    private val _currenciesStream: MutableLiveData<List<Currency>> = MutableLiveData(emptyList())
    val currenciesStream: LiveData<List<Currency>> = _currenciesStream

    private val _errorStateStream: MutableLiveData<Error> = MutableLiveData(Error.None)
    val errorStateStream: LiveData<Error> = _errorStateStream

    private val _showLoadingIndicatorStream: MutableLiveData<Boolean> = MutableLiveData(false)
    val showLoadingIndicatorStream: LiveData<Boolean> = _showLoadingIndicatorStream

    init {
        loadCurrencies()
    }

    fun retrySelected() = loadCurrencies()

    fun currencySelected(currency: Currency) = viewModelScope.launch {
        updateCurrencyPreference.runHandlingFailure(currency)
    }

    private fun loadCurrencies() = viewModelScope.launch(Dispatchers.Default) {
        _showLoadingIndicatorStream.postValue(true)
        _errorStateStream.postValue(Error.None)

        val currenciesOperation = getSupportedCurrencies()

        if (currenciesOperation.isLeft()) {
            _errorStateStream.postValue(Error.CurrenciesNotLoaded)
        } else {
            val localCurrency = Currency.getInstance(localeProvider.getDefault()).orUSD()

            currenciesOperation.requireRight()
                .filter { it != localCurrency }
                .sortedBy { it.displayName }
                .reversed()
                .let { currencies -> listOf(localCurrency) + currencies }
                .also { currencies -> _currenciesStream.postValue(currencies) }
        }

        _showLoadingIndicatorStream.postValue(false)
    }

    sealed class Error {
        object CurrenciesNotLoaded : Error()
        object None : Error()
    }
}