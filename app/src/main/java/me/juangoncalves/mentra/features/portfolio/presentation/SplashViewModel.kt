package me.juangoncalves.mentra.features.portfolio.presentation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.core.extensions.TAG
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.portfolio.domain.usecases.GetCoinsUseCase
import me.juangoncalves.mentra.features.portfolio.presentation.SplashViewModel.State.Loading

class SplashViewModel @ViewModelInject constructor(
    private val getCoins: GetCoinsUseCase,
    private val logger: Logger
) : ViewModel() {

    sealed class State {
        object Loading : State()

        object Loaded : State()

        class Error(val message: String) : State()
    }

    val viewState: LiveData<State> get() = _viewState

    private val _viewState: MutableLiveData<State> = MutableLiveData(Loading)


    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.postValue(Loading)
            val result = getCoins.execute()
            val state = result.fold(
                left = { failure -> State.Error("Some error message") },
                right = { State.Loaded }
            )
            _viewState.postValue(state)
        }
    }

    fun retryInitialization() {
        logger.info(TAG, "Retrying initialization")
        fetchCoins()
    }

}