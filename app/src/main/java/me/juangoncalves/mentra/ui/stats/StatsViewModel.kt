package me.juangoncalves.mentra.ui.stats

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.usecases.portfolio.GetPortfolioDistributionStream
import me.juangoncalves.mentra.domain.usecases.portfolio.GetPortfolioValueHistoryStream
import me.juangoncalves.mentra.domain.usecases.portfolio.RefreshPortfolioValue
import me.juangoncalves.mentra.ui.common.FleetingErrorPublisher
import me.juangoncalves.mentra.ui.common.FleetingErrorPublisherImpl
import me.juangoncalves.mentra.ui.common.executor
import me.juangoncalves.mentra.ui.common.run
import me.juangoncalves.pie.PiePortion
import java.time.LocalDate
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

// Group a list of entries with a map to build the labels for the time axis
typealias TimeChartData = Pair<List<Entry>, Map<Int, LocalDate>>

class StatsViewModel @ViewModelInject constructor(
    getPortfolioValueHistory: GetPortfolioValueHistoryStream,
    getPortfolioDistribution: GetPortfolioDistributionStream,
    private val refreshPortfolioValue: RefreshPortfolioValue
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val valueChartData: LiveData<TimeChartData> = getPortfolioValueHistory()
        .toTimeChartData()
        .flowOn(Dispatchers.Default)
        .asLiveData()

    val pieChartData: LiveData<Array<PiePortion>> = getPortfolioDistribution()
        .toPiePortions()
        .flowOn(Dispatchers.Default)
        .asLiveData()

    val shouldShowRefreshIndicator: LiveData<Boolean> get() = _shouldShowRefreshIndicator

    private val _shouldShowRefreshIndicator: MutableLiveData<Boolean> = MutableLiveData(false)

    fun refreshSelected() {
        refreshPortfolioValue.executor()
            .inScope(viewModelScope)
            .beforeInvoke { _shouldShowRefreshIndicator.postValue(true) }
            .afterInvoke { _shouldShowRefreshIndicator.postValue(false) }
            .onFailurePublishFleetingError()
            .run()
    }

    private fun Flow<List<Price>>.toTimeChartData() = map { prices ->
        val indexToDate = hashMapOf<Int, LocalDate>()
        val entries = prices.mapIndexed { index, price ->
            indexToDate[index] = price.date.toLocalDate()
            Entry(index.toFloat(), price.value.toFloat())
        }
        Pair(entries, indexToDate)
    }

    private fun Flow<Map<Coin, Double>>.toPiePortions() = map { coinPercentages ->
        coinPercentages.entries.map { (coin, value) ->
            PiePortion(value, coin.symbol)
        }.toTypedArray()
    }

}