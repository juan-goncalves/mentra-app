package me.juangoncalves.mentra.ui.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.usecases.GetPortfolioValueUseCase
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Event

class DashboardViewModel @ViewModelInject constructor(
    private val getPortfolioValue: GetPortfolioValueUseCase
) : ViewModel() {

    val portfolioValue: LiveData<Price> get() = _portfolioValue
    val error: LiveData<DisplayError> get() = _error
    val openedTab: LiveData<Tab> get() = _openedTab
    val closeEvent: LiveData<Event<Unit>> get() = _closeEvent

    private val _portfolioValue: MutableLiveData<Price> = MutableLiveData(Price.None)
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private val _openedTab: MutableLiveData<Tab> = MutableLiveData(Tab.STATS)
    private val _closeEvent: MutableLiveData<Event<Unit>> = MutableLiveData()

    init {
        refreshPortfolioValue()
    }

    fun openStatsSelected() {
        _openedTab.value = Tab.STATS
    }

    fun openWalletsSelected() {
        _openedTab.value = Tab.WALLETS
    }

    fun backPressed() {
        val currentTab = _openedTab.value ?: return

        when (currentTab) {
            Tab.STATS -> _closeEvent.value = Event(Unit)
            else -> _openedTab.value = Tab.STATS
        }
    }

    private fun refreshPortfolioValue() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPortfolioValue()
        val value = result.fold(
            left = { Price.None },
            right = { it }
        )
        _portfolioValue.postValue(value)
    }

    enum class Tab { STATS, WALLETS }

}
