package me.juangoncalves.mentra.ui.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.extensions.toPrice
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Event

typealias LastDayChangeData = Pair<Price, Double>

class DashboardViewModel @ViewModelInject constructor(
    portfolioRepository: PortfolioRepository
) : ViewModel() {

    val portfolioValue: LiveData<Price> get() = _portfolioValue
    val lastDayValueChange: LiveData<LastDayChangeData> get() = _lastDayValueChange
    val error: LiveData<DisplayError> get() = _error
    val openedTab: LiveData<Tab> get() = _openedTab
    val closeEvent: LiveData<Event<Unit>> get() = _closeEvent

    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private val _openedTab: MutableLiveData<Tab> = MutableLiveData(Tab.STATS)
    private val _closeEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    private val _portfolioValue: LiveData<Price> = portfolioRepository.portfolioValue.asLiveData()

    private val _lastDayValueChange: LiveData<LastDayChangeData> =
        portfolioRepository.portfolioValueHistory
            .lastDayPercentChange()
            .asLiveData()

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

    private fun Flow<List<Price>>.lastDayPercentChange(): Flow<LastDayChangeData> = map { prices ->
        if (prices.size < 2) return@map LastDayChangeData(0.0.toPrice(), 0.0)

        val latestValue = prices[prices.lastIndex]
        val previousDayValue = prices[prices.lastIndex - 1]

        val percentChange = latestValue.value / previousDayValue.value - 1
        val valueDifference = latestValue.value - previousDayValue.value
        LastDayChangeData(valueDifference.toPrice(), percentChange)
    }

    enum class Tab { STATS, WALLETS }

}
