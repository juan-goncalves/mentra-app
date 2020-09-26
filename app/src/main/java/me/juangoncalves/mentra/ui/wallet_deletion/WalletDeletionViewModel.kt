package me.juangoncalves.mentra.ui.wallet_deletion

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Notification

class WalletDeletionViewModel @ViewModelInject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    val dismiss: LiveData<Notification> get() = _dismiss
    val onError: LiveData<DisplayError> get() = _error
    val deletedWallet: Boolean get() = _deletedWallet

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private var _deletedWallet: Boolean = false

    fun onDeleteSelected(wallet: Wallet) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = walletRepository.deleteWallet(wallet)
            if (result.isLeft()) {
                val error = DisplayError(R.string.default_error) {
                    onDeleteSelected(wallet)
                }
                _error.postValue(error)
            } else {
                _deletedWallet = true
                _dismiss.postValue(Notification())
            }
        }
    }

    fun onCancelSelected() {
        _dismiss.postValue(Notification())
    }

}