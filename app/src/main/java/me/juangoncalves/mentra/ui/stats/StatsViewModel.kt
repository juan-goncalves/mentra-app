package me.juangoncalves.mentra.ui.stats

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.pie.PiePortion
import java.time.LocalDate
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

// Group a list of entries with a map to build the labels for the time axis
typealias TimeChartData = Pair<List<Entry>, Map<Int, LocalDate>>

class StatsViewModel @ViewModelInject constructor(
    portfolioRepository: PortfolioRepository
) : ViewModel() {

    val valueChartData: LiveData<TimeChartData> = portfolioRepository.portfolioValueHistory
        .toTimeChartData()
        .flowOn(Dispatchers.Default)
        .asLiveData()

    val pieChartData: LiveData<Array<PiePortion>> = portfolioRepository.portfolioDistribution
        .toPiePortions()
        .flowOn(Dispatchers.Default)
        .asLiveData()

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