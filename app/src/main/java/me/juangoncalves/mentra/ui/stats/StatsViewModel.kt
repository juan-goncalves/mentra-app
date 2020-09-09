package me.juangoncalves.mentra.ui.stats

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.usecases.GetPortfolioValueHistoryUseCase
import me.juangoncalves.mentra.extensions.rightValue
import java.time.LocalDate

class StatsViewModel @ViewModelInject constructor(
    private val getPortfolioValueHistory: GetPortfolioValueHistoryUseCase
) : ViewModel() {

    val portfolioValueHistory: LiveData<Map<LocalDate, Double>> get() = _portfolioValueHistory

    private val _portfolioValueHistory: MutableLiveData<Map<LocalDate, Double>> = MutableLiveData()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.Default) {
            val result = getPortfolioValueHistory()
            result.rightValue?.let {
                _portfolioValueHistory.postValue(it)
            }
        }
    }

}