package me.juangoncalves.mentra.features.stats.model

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
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
import me.juangoncalves.pie.PiePortion
import java.util.*
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
) : ViewModel(), FailurePublisher by GeneralFailurePublisher() {

    private val portfolioValueHistory = getPortfolioValueHistory()

    val valueChartData = with(exchangePriceStream) {
        portfolioValueHistory
            .exchangeWhenPreferredCurrencyChanges()
            .toTimeChartData()
            .asLiveData()
    }

    val placeholderPortfolioChartData = with(exchangePriceStream) {
        portfolioValueHistory
            .map { data -> if (data.isEmpty()) placeholderPrices else emptyList() }
            .exchangeWhenPreferredCurrencyChanges()
            .toTimeChartData()
            .asLiveData()
    }

    val pieChartData = getPortfolioDistribution().toPiePortions().asLiveData()

    val placeholderPieChartData = pieChartData.map { portions ->
        if (portions.isEmpty()) placeholderPortions else portions
    }

    val valueChartGranularityStream = getTimeUnitPrefStream().asLiveData()
    val shouldShowRefreshIndicator = MutableLiveData(false)
    val shouldShowDistributionPlaceholder: LiveData<Boolean> = pieChartData.map { it.isEmpty() }
    val shouldShowDistributionChart: LiveData<Boolean> = pieChartData.map { it.isNotEmpty() }

    val shouldShowHistoricPortfolioValuePlaceholder: LiveData<Boolean> =
        portfolioValueHistory.map(List<Price>::isEmpty).asLiveData()

    val enableTimeGranularitySelection: LiveData<Boolean> =
        portfolioValueHistory.map(List<Price>::isNotEmpty).asLiveData()

    val shouldShowHistoricPortfolioValue: LiveData<Boolean> = valueChartData.map { data ->
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

private val placeholderPrices: List<Price> = Currency.getInstance("USD").let { usd ->
    listOf(
        Price(2643.4.toBigDecimal(), usd, LocalDateTime(2022, 3, 12, 23, 59)),
        Price(2932.0.toBigDecimal(), usd, LocalDateTime(2022, 3, 13, 23, 59)),
        Price(3156.9.toBigDecimal(), usd, LocalDateTime(2022, 3, 14, 23, 59)),
        Price(2832.4.toBigDecimal(), usd, LocalDateTime(2022, 3, 15, 23, 59)),
        Price(2687.7.toBigDecimal(), usd, LocalDateTime(2022, 3, 16, 23, 59)),
        Price(3000.4.toBigDecimal(), usd, LocalDateTime(2022, 3, 17, 23, 59)),
        Price(2892.4.toBigDecimal(), usd, LocalDateTime(2022, 3, 18, 23, 59)),
        Price(3212.9.toBigDecimal(), usd, LocalDateTime(2022, 3, 19, 23, 59)),
    )
}

private val placeholderPortions: Array<PiePortion> = arrayOf(
    PiePortion(0.6, "BTC"),
    PiePortion(0.25, "ETH"),
    PiePortion(0.15, "ADA"),
)
