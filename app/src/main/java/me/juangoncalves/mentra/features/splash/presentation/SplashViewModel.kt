package me.juangoncalves.mentra.features.splash.presentation

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.core.errors.InternetConnectionFailure
import me.juangoncalves.mentra.core.extensions.TAG
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.features.portfolio.domain.usecases.GetCoinsUseCase
import me.juangoncalves.mentra.features.splash.presentation.SplashViewModel.State.Loading

class SplashViewModel @ViewModelInject constructor(
    private val getCoins: GetCoinsUseCase,
    private val logger: Logger
) : ViewModel() {

    val viewState: LiveData<State> get() = _viewState
    val eventChannel: Channel<Event> = Channel()

    private val _viewState: MutableLiveData<State> = MutableLiveData(Loading)

    init {
        fetchCoins()
    }

    fun retryInitialization() {
        logger.info(TAG, "Retrying initialization")
        fetchCoins()
    }

    private fun fetchCoins() = viewModelScope.launch(Dispatchers.IO) {
        _viewState.postValue(Loading)
        val deferredCoins = async { getCoins.execute() }
        // Force the splash screen to be shown for at least one second
        val delay = async { delay(1000L) }
        delay.await()
        when (val result = deferredCoins.await()) {
            is Either.Left -> _viewState.postValue(failureToErrorState(result.value))
            is Either.Right -> eventChannel.send(Event.NavigateToPortfolio)
        }
    }

    private fun failureToErrorState(failure: Failure): State.Error {
        return when (failure) {
            is InternetConnectionFailure -> State.Error(R.string.connection_error)
            else -> State.Error(R.string.default_error)
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventChannel.close()
    }

    sealed class State {
        object Loading : State()

        class Error(@StringRes val messageId: Int) : State()
    }

    sealed class Event {
        object NavigateToPortfolio : Event()
    }
}