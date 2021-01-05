package me.juangoncalves.mentra.features.dashboard

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import me.juangoncalves.mentra.common.BundleKeys
import me.juangoncalves.mentra.common.Notification
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.ValueVariation
import me.juangoncalves.mentra.domain_layer.usecases.currency.ExchangePriceStream
import me.juangoncalves.mentra.domain_layer.usecases.currency.ExchangeVariationStream
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.GetPortfolioValueStream
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.GetPortfolioValueVariationStream


class DashboardViewModel @ViewModelInject constructor(
    getPortfolioValue: GetPortfolioValueStream,
    getPortfolioValueVariation: GetPortfolioValueVariationStream,
    exchangeExchangePriceStream: ExchangePriceStream,
    exchangeExchangeVariationStream: ExchangeVariationStream
) : ViewModel() {

    val portfolioValue: LiveData<Price> get() = _portfolioValue
    val lastDayValueChange: LiveData<ValueVariation> get() = _lastDayValueChange
    val openedTab: LiveData<Tab> get() = _openedTab
    val closeEvent: LiveData<Notification> get() = _closeEvent

    private val _openedTab: MutableLiveData<Tab> = MutableLiveData()
    private val _closeEvent: MutableLiveData<Notification> = MutableLiveData()

    private val _portfolioValue: LiveData<Price> = with(exchangeExchangePriceStream) {
        getPortfolioValue()
            .exchangeWhenPreferredCurrencyChanges()
            .asLiveData()
    }

    private val _lastDayValueChange: LiveData<ValueVariation> =
        with(exchangeExchangeVariationStream) {
            getPortfolioValueVariation()
                .exchangeWhenPreferredCurrencyChanges()
                .asLiveData()
        }

    fun initialize(args: Bundle?) {
        val isFirstRun = args?.getBoolean(BundleKeys.FirstRun) ?: false
        _openedTab.value = if (isFirstRun) Tab.Wallets else Tab.Stats
    }

    fun openStatsSelected() {
        _openedTab.value = Tab.Stats
    }

    fun openWalletsSelected() {
        _openedTab.value = Tab.Wallets
    }

    fun backPressed() {
        val currentTab = _openedTab.value ?: return

        when (currentTab) {
            Tab.Stats -> _closeEvent.value = Notification()
            else -> _openedTab.value = Tab.Stats
        }
    }

    enum class Tab { Stats, Wallets }

}

