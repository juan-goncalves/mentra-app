package me.juangoncalves.mentra.features.stats.model

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
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
import me.juangoncalves.mentra.failures.FailurePublisher
import me.juangoncalves.mentra.failures.GeneralFailurePublisher
import me.juangoncalves.mentra.features.stats.mapper.PiePortionMapper
import me.juangoncalves.mentra.features.stats.mapper.TimeChartMapper
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    getPortfolioValueHistory: GetPortfolioValueHistoryStream,
    getPortfolioDistribution: GetPortfolioDistributionStream,
    getTimeUnitPrefStream: GetTimeUnitPreferenceStream,
    exchangePriceStream: ExchangePriceStream,
    private val refreshPortfolioValue: RefreshPortfolioValue,
    private val updatePortfolioValueTimeGranularity: UpdatePortfolioValueTimeGranularity,
    private val timeChartMapper: TimeChartMapper,
    private val piePortionMapper: PiePortionMapper
) : ViewModel(),
    FailurePublisher by GeneralFailurePublisher() {

    val valueChartData = with(exchangePriceStream) {
        getPortfolioValueHistory()
            .exchangeWhenPreferredCurrencyChanges()
            .toTimeChartData()
            .asLiveData()
    }

    val pieChartData = getPortfolioDistribution().toPiePortions().asLiveData()
    val valueChartGranularityStream = getTimeUnitPrefStream().asLiveData()
    val shouldShowRefreshIndicator = MutableLiveData(false)
    val shouldShowEmptyPortionsWarning: LiveData<Boolean> = pieChartData.map { it.isEmpty() }
    val shouldShowPieChart: LiveData<Boolean> = pieChartData.map { it.isNotEmpty() }

    val shouldShowEmptyLineChartWarning: LiveData<Boolean> = valueChartData.map { data ->
        data.entries.isEmpty()
    }

    val shouldShowLineChart: LiveData<Boolean> = valueChartData.map { data ->
        data.entries.isNotEmpty()
    }

    fun refreshSelected() = viewModelScope.launch {
        shouldShowRefreshIndicator.postValue(true)
        refreshPortfolioValue.runHandlingFailure(Unit)
        shouldShowRefreshIndicator.postValue(false)
    }

    fun timeGranularityChanged(selection: TimeGranularity) = viewModelScope.launch {
        updatePortfolioValueTimeGranularity(selection)
    }

    private fun Flow<List<Price>>.toTimeChartData() = map { timeChartMapper.map(it) }

    private fun Flow<Map<Coin, Double>>.toPiePortions() = map { piePortionMapper.map(it) }

}