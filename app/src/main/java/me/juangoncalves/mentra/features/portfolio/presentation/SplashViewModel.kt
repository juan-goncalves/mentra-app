package me.juangoncalves.mentra.features.portfolio.presentation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.features.portfolio.domain.usecases.GetCoinsUseCase
import me.juangoncalves.mentra.features.portfolio.presentation.SplashViewModel.State.Loading

class SplashViewModel @ViewModelInject constructor(
    private val getCoins: GetCoinsUseCase
) : ViewModel() {

    sealed class State {

        object Loading : State()

        class Error(val message: String) : State()

        object Loaded : State()

    }

    val stateLiveData: LiveData<State> get() = _stateLiveData

    private val _stateLiveData: MutableLiveData<State> = MutableLiveData(Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getCoins.execute()
            val resultingState = result.fold(
                left = { failure -> State.Error("Some error message") },
                right = { State.Loaded }
            )
            _stateLiveData.postValue(resultingState)
        }
    }

}