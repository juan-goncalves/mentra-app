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

class DashboardViewModel @ViewModelInject constructor(
    private val getPortfolioValue: GetPortfolioValueUseCase
) : ViewModel() {

    val portfolioValue: LiveData<Price> get() = _portfolioValue
    val error: LiveData<DisplayError> get() = _error
    val openedTab: LiveData<Tab> get() = _openedTab

    private val _portfolioValue: MutableLiveData<Price> = MutableLiveData(Price.None)
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private val _openedTab: MutableLiveData<Tab> = MutableLiveData(Tab.WALLETS)

    init {
        refreshPortfolioValue()
    }

    fun openStatsScreen() {
        _openedTab.value = Tab.STATS
    }

    fun openWalletsScreen() {
        _openedTab.value = Tab.WALLETS
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
