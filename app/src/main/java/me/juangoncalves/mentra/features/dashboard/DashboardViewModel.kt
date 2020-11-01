package me.juangoncalves.mentra.features.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.ValueVariation
import me.juangoncalves.mentra.domain.usecases.portfolio.GetPortfolioValueStream
import me.juangoncalves.mentra.domain.usecases.portfolio.GetPortfolioValueVariationStream
import me.juangoncalves.mentra.features.common.DisplayError
import me.juangoncalves.mentra.features.common.Event


class DashboardViewModel @ViewModelInject constructor(
    getPortfolioValue: GetPortfolioValueStream,
    getPortfolioValueVariation: GetPortfolioValueVariationStream
) : ViewModel() {

    val portfolioValue: LiveData<Price> get() = _portfolioValue
    val lastDayValueChange: LiveData<ValueVariation> get() = _lastDayValueChange
    val error: LiveData<DisplayError> get() = _error
    val openedTab: LiveData<Tab> get() = _openedTab
    val closeEvent: LiveData<Event<Unit>> get() = _closeEvent

    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private val _openedTab: MutableLiveData<Tab> = MutableLiveData(Tab.STATS)
    private val _closeEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    private val _portfolioValue: LiveData<Price> = getPortfolioValue().asLiveData()

    private val _lastDayValueChange: LiveData<ValueVariation> =
        getPortfolioValueVariation(ValueVariation.TimeUnit.Daily).asLiveData()

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

    enum class Tab { STATS, WALLETS }

}
