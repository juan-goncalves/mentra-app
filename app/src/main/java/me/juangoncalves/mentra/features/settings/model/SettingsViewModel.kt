package me.juangoncalves.mentra.features.settings.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain_layer.extensions.isLeft
import me.juangoncalves.mentra.domain_layer.extensions.requireRight
import me.juangoncalves.mentra.domain_layer.usecases.coin.RefreshSupportedCoins
import me.juangoncalves.mentra.domain_layer.usecases.currency.GetSupportedCurrencies
import me.juangoncalves.mentra.features.common.Event
import java.util.*

class SettingsViewModel @ViewModelInject constructor(
    private val getSupportedCurrencies: GetSupportedCurrencies,
    private val refreshSupportedCoins: RefreshSupportedCoins
) : ViewModel() {

    val availableCurrenciesStream: LiveData<List<Currency>> get() = _availableCurrenciesStream
    val durationsStream: LiveData<List<RefreshPeriod>> get() = _durationsStream
    val showLoadingIndicatorStream: LiveData<Boolean> get() = _showLoadingIndicatorStream
    val showSuccessSnackbarStream: LiveData<Event<Int>> get() = _showSuccessSnackbarStream
    val showErrorSnackbarStream: LiveData<Event<Int>> get() = _showErrorSnackbarStream

    private val _availableCurrenciesStream: MutableLiveData<List<Currency>> = MutableLiveData()
    private val _durationsStream: MutableLiveData<List<RefreshPeriod>> = MutableLiveData()
    private val _showLoadingIndicatorStream: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _showSuccessSnackbarStream: MutableLiveData<Event<Int>> = MutableLiveData()
    private val _showErrorSnackbarStream: MutableLiveData<Event<Int>> = MutableLiveData()

    init {
        loadCurrencies()
        loadPeriodicRefreshOptions()
    }

    private fun loadPeriodicRefreshOptions() {
        _durationsStream.value = listOf(
            RefreshPeriod(R.string.every_3_hours, "3"),
            RefreshPeriod(R.string.every_6_hours, "6"),
            RefreshPeriod(R.string.every_12_hours, "12"),
            RefreshPeriod(R.string.once_a_day, "24")
        )
    }

    fun refreshCoinsSelected() {
        viewModelScope.launch {
            _showLoadingIndicatorStream.value = true
            val result = refreshSupportedCoins()

            if (result.isLeft()) {
                _showErrorSnackbarStream.value = Event(R.string.coin_list_refresh_error)
            } else {
                _showSuccessSnackbarStream.value = Event(R.string.coins_updated)
            }

            _showLoadingIndicatorStream.value = false
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            val result = getSupportedCurrencies.execute()

            if (result.isLeft()) {
                _showErrorSnackbarStream.value = Event(R.string.error_loading_currencies)
            } else {
                _availableCurrenciesStream.value = result.requireRight()
                    .sortedBy { it.currencyCode }
                    .toList()
            }
        }
    }

}