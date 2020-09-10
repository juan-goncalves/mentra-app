package me.juangoncalves.mentra.ui.stats

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.usecases.GetPortfolioValueHistoryUseCase
import me.juangoncalves.mentra.extensions.rightValue
import java.time.LocalDate

// Group a list of entries with a map to build the labels for the time axis
typealias TimeChartData = Pair<List<Entry>, Map<Float, LocalDate>>

class StatsViewModel @ViewModelInject constructor(
    private val getPortfolioValueHistory: GetPortfolioValueHistoryUseCase
) : ViewModel() {

    val valueChartData: LiveData<TimeChartData> get() = _valueChartData

    private val _valueChartData: MutableLiveData<TimeChartData> = MutableLiveData()

    init {
        loadPortfolioValueChart()
    }

    private fun loadPortfolioValueChart() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = getPortfolioValueHistory()
            result.rightValue?.let { valuesByDate ->
                val indexToDate = hashMapOf<Float, LocalDate>()
                val entries = valuesByDate.entries.mapIndexed { index, entry ->
                    val (date, value) = entry
                    val indexAsFloat = index.toFloat()
                    indexToDate[indexAsFloat] = date
                    Entry(indexAsFloat, value.toFloat())
                }
                _valueChartData.postValue(Pair(entries, indexToDate))
            }
        }
    }

}