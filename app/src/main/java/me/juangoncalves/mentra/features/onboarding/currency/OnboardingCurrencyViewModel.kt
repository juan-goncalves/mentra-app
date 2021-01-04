package me.juangoncalves.mentra.features.onboarding.currency

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.extensions.isLeft
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.usecases.currency.GetSupportedCurrencies
import java.util.*

class OnboardingCurrencyViewModel @ViewModelInject constructor(
    private val getSupportedCurrencies: GetSupportedCurrencies
) : ViewModel() {

    private val _currenciesStream: MutableLiveData<List<Currency>> = MutableLiveData(emptyList())
    val currenciesStream: MutableLiveData<List<Currency>> = _currenciesStream

    private val _errorStateStream: MutableLiveData<Error> = MutableLiveData(Error.None)
    val errorStateStream: MutableLiveData<Error> = _errorStateStream

    init {
        loadCurrencies()
    }

    fun retrySelected() = loadCurrencies()
    
    private fun loadCurrencies() = viewModelScope.launch(Dispatchers.Default) {
        val currenciesOp = getSupportedCurrencies()
        if (currenciesOp.isLeft()) {
            _errorStateStream.postValue(Error.CurrenciesNotLoaded)
        } else {
            val sorted = currenciesOp.requireRight().toList().sortedBy { it.displayName }.reversed()
            _currenciesStream.postValue(sorted)
            _errorStateStream.postValue(Error.None)
        }
    }

    sealed class Error {
        object CurrenciesNotLoaded : Error()
        object None : Error()
    }

}