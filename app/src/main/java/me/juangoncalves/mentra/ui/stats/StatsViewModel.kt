package me.juangoncalves.mentra.ui.stats

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.usecases.CalculatePortfolioDistributionUseCase
import me.juangoncalves.mentra.domain.usecases.GetPortfolioValueHistoryUseCase
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.pie.PiePortion
import java.time.LocalDate

// Group a list of entries with a map to build the labels for the time axis
typealias TimeChartData = Pair<List<Entry>, Map<Int, LocalDate>>

class StatsViewModel @ViewModelInject constructor(
    private val getPortfolioValueHistory: GetPortfolioValueHistoryUseCase,
    private val calculatePortfolioDistribution: CalculatePortfolioDistributionUseCase
) : ViewModel() {

    val valueChartData: LiveData<TimeChartData> get() = _valueChartData
    val distributionChartData: LiveData<Array<PiePortion>> get() = _distributionChartData

    private val _valueChartData: MutableLiveData<TimeChartData> = MutableLiveData()
    private val _distributionChartData: MutableLiveData<Array<PiePortion>> = MutableLiveData()

    init {
        loadPortfolioValueChart()
        loadPortfolioDistributionChart()
    }

    private fun loadPortfolioValueChart() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = getPortfolioValueHistory()
            result.rightValue?.let { valuesByDate ->
                val indexToDate = hashMapOf<Int, LocalDate>()
                val entries = valuesByDate.entries.mapIndexed { index, entry ->
                    val (date, value) = entry
                    indexToDate[index] = date
                    Entry(index.toFloat(), value.toFloat())
                }
                _valueChartData.postValue(Pair(entries, indexToDate))
            }
        }
    }

    private fun loadPortfolioDistributionChart() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = calculatePortfolioDistribution()
            result.rightValue?.entries?.map { (coin, value) ->
                PiePortion(value, coin.symbol)
            }
                ?.also { _distributionChartData.postValue(it.toTypedArray()) }
        }
    }

}