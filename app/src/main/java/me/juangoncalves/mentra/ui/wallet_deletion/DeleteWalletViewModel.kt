package me.juangoncalves.mentra.ui.wallet_deletion

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.ui.common.DefaultErrorHandler
import me.juangoncalves.mentra.ui.common.DefaultErrorHandlerImpl
import me.juangoncalves.mentra.ui.common.Notification

class DeleteWalletViewModel @ViewModelInject constructor(
    private val _deleteWallet: DeleteWallet
) : ViewModel(), DefaultErrorHandler by DefaultErrorHandlerImpl() {

    val dismiss: LiveData<Notification> get() = _dismiss
    val deletedWallet: Boolean get() = _deletedWallet

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private var _deletedWallet: Boolean = false

    fun onDeleteSelected(wallet: Wallet) {
        _deleteWallet.prepare()
            .withDispatcher(Dispatchers.IO)
            .inScope(viewModelScope)
            .onSuccess {
                _deletedWallet = true
                _dismiss.postValue(Notification())
            }
            .run(wallet)
    }

    fun onCancelSelected() {
        _dismiss.postValue(Notification())
    }

    override fun onCleared() {
        super.onCleared()
        dispose()
    }

}