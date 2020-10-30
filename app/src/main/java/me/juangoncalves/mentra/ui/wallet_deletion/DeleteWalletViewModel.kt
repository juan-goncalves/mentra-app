package me.juangoncalves.mentra.ui.wallet_deletion

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.juangoncalves.mentra.domain.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.ui.common.*
import me.juangoncalves.mentra.ui.wallet_list.WalletListViewState

class DeleteWalletViewModel @ViewModelInject constructor(
    private val deleteWallet: DeleteWallet
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val dismissStream: LiveData<Notification> get() = _dismissStream

    var walletWasDeleted: Boolean = false
        private set

    lateinit var wallet: WalletListViewState.Wallet
        private set

    private val _dismissStream: MutableLiveData<Notification> = MutableLiveData()

    fun initialize(args: Bundle?) {
        wallet = args?.getParcelable(BundleKeys.Wallet)
            ?: error("You must provide the wallet to delete")
    }

    fun deleteSelected() {
        val params = DeleteWallet.Params(wallet.id)

        deleteWallet.executor()
            .inScope(viewModelScope)
            .onSuccess {
                walletWasDeleted = true
                _dismissStream.postValue(Notification())
            }
            .onFailurePublishFleetingError()
            .run(params)
    }

    fun cancelSelected() {
        _dismissStream.postValue(Notification())
    }

}