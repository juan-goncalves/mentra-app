package me.juangoncalves.mentra.features.portfolio.presentation

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.features.portfolio.domain.usecases.GetCoinsUseCase
import me.juangoncalves.mentra.features.portfolio.presentation.SplashViewModel.State.Loading

class SplashViewModel @ViewModelInject constructor(
    private val getCoins: GetCoinsUseCase
) : ViewModel() {

    sealed class State {
        object Loading : State()

        object Loaded : State()

        class Error(val message: String) : State()
    }

    var viewState: State by mutableStateOf<State>(Loading)
        private set


    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getCoins.execute()
            withContext(Dispatchers.Main) {
                viewState = result.fold(
                    left = { failure -> State.Error("Some error message") },
                    right = { State.Loaded }
                )
            }
        }
    }

}