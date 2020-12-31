package me.juangoncalves.mentra.features.stats.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.TimeGranularity
import me.juangoncalves.mentra.domain_layer.usecases.currency.ExchangePriceStream
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.GetPortfolioDistributionStream
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.GetPortfolioValueHistoryStream
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.RefreshPortfolioValue
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetTimeUnitPreferenceStream
import me.juangoncalves.mentra.domain_layer.usecases.preference.UpdatePortfolioValueTimeGranularity
import me.juangoncalves.mentra.failures.FailureHandler
import me.juangoncalves.mentra.failures.GeneralFailureHandler
import me.juangoncalves.mentra.features.stats.mapper.PiePortionMapper
import me.juangoncalves.mentra.features.stats.mapper.TimeChartMapper
import me.juangoncalves.pie.PiePortion

class StatsViewModel @ViewModelInject constructor(
    getPortfolioValueHistory: GetPortfolioValueHistoryStream,
    getPortfolioDistribution: GetPortfolioDistributionStream,
    getTimeUnitPreferenceStream: GetTimeUnitPreferenceStream,
    exchangePriceStream: ExchangePriceStream,
    private val refreshPortfolioValue: RefreshPortfolioValue,
    private val updatePortfolioValueTimeGranularity: UpdatePortfolioValueTimeGranularity,
    private val timeChartMapper: TimeChartMapper,
    private val piePortionMapper: PiePortionMapper
) : ViewModel(),
    FailureHandler by GeneralFailureHandler() {

    val valueChartData: LiveData<TimeChartData> = with(exchangePriceStream) {
        getPortfolioValueHistory()
            .exchangeWhenPreferredCurrencyChanges()
            .toTimeChartData()
            .asLiveData()
    }

    val pieChartData: LiveData<Array<PiePortion>> = getPortfolioDistribution()
        .toPiePortions()
        .asLiveData()

    val shouldShowRefreshIndicator: MutableLiveData<Boolean> = MutableLiveData(false)

    val valueChartGranularityStream: LiveData<TimeGranularity> =
        getTimeUnitPreferenceStream().asLiveData()

    fun refreshSelected() = viewModelScope.launch {
        shouldShowRefreshIndicator.postValue(true)
        refreshPortfolioValue.runHandlingFailure(Unit)
        shouldShowRefreshIndicator.postValue(false)
    }

    fun timeGranularityChanged(selection: TimeGranularity) {
        viewModelScope.launch {
            updatePortfolioValueTimeGranularity(selection)
        }
    }

    private fun Flow<List<Price>>.toTimeChartData() = map { timeChartMapper.map(it) }

    private fun Flow<Map<Coin, Double>>.toPiePortions() = map { piePortionMapper.map(it) }

}