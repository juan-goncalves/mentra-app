package me.juangoncalves.mentra.ui.wallet_edit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Notification

class WalletEditViewModel @ViewModelInject constructor() : ViewModel() {

    val dismiss: LiveData<Notification> get() = _dismiss
    val onError: LiveData<DisplayError> get() = _error
    val savedUpdates: Boolean get() = _savedUpdates

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private var _savedUpdates: Boolean = false

    fun onEditSelected(wallet: Wallet) {

    }

    fun onCancelSelected() {
        _dismiss.postValue(Notification())
    }

}