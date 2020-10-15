package me.juangoncalves.mentra.ui.wallet_deletion

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.ui.common.FleetingErrorPublisher
import me.juangoncalves.mentra.ui.common.FleetingErrorPublisherImpl
import me.juangoncalves.mentra.ui.common.Notification
import me.juangoncalves.mentra.ui.common.executor

class DeleteWalletViewModel @ViewModelInject constructor(
    private val _deleteWallet: DeleteWallet
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val dismiss: LiveData<Notification> get() = _dismiss
    val deletedWallet: Boolean get() = _deletedWallet

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private var _deletedWallet: Boolean = false

    fun onDeleteSelected(wallet: Wallet) {
        _deleteWallet.executor()
            .withDispatcher(Dispatchers.IO)
            .inScope(viewModelScope)
            .onSuccess {
                _deletedWallet = true
                _dismiss.postValue(Notification())
            }
            .onFailurePublishFleetingError()
            .run(wallet)
    }

    fun onCancelSelected() {
        _dismiss.postValue(Notification())
    }

}