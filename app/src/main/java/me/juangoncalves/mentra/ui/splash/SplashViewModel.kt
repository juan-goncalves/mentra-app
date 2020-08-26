package me.juangoncalves.mentra.ui.splash

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.InternetConnectionFailure
import me.juangoncalves.mentra.domain.usecases.GetCoinsUseCase
import me.juangoncalves.mentra.log.Logger
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Event

class SplashViewModel @ViewModelInject constructor(
    private val getCoins: GetCoinsUseCase,
    private val logger: Logger
) : ViewModel() {

    val shouldShowProgressBar: LiveData<Boolean> get() = _shouldShowProgressBar
    val navigateToDashboard: LiveData<Event<Unit>> get() = _navigateToDashboard
    val error: LiveData<DisplayError> get() = _error

    private val _shouldShowProgressBar: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _navigateToDashboard: MutableLiveData<Event<Unit>> = MutableLiveData()
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()

    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            _shouldShowProgressBar.postValue(true)
            val deferredCoins = async { getCoins() }
            // Force the splash screen to be shown for at least one second
            val delay = async { delay(1300L) }
            delay.await()
            when (val result = deferredCoins.await()) {
                is Either.Left -> {
                    val error = DisplayError(failureToMessageId(result.value), ::fetchCoins)
                    _error.postValue(error)
                }
                is Either.Right -> _navigateToDashboard.postValue(Event(Unit))
            }
            _shouldShowProgressBar.postValue(false)
        }
    }

    @StringRes
    private fun failureToMessageId(failure: Failure): Int {
        return when (failure) {
            is InternetConnectionFailure -> R.string.connection_error
            else -> R.string.default_error
        }
    }

}