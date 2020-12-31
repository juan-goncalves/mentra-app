package me.juangoncalves.mentra.features.wallet_deletion

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.failures.FailureHandler
import me.juangoncalves.mentra.failures.GeneralFailureHandler
import me.juangoncalves.mentra.features.common.BundleKeys
import me.juangoncalves.mentra.features.common.Notification
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState

class DeleteWalletViewModel @ViewModelInject constructor(
    private val deleteWallet: DeleteWallet
) : ViewModel(), FailureHandler by GeneralFailureHandler() {

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

    fun deleteSelected() = viewModelScope.launch {
        val params = DeleteWallet.Params(wallet.id)
        deleteWallet.runHandlingFailure(params) {
            walletWasDeleted = true
            _dismissStream.postValue(Notification())
        }
    }

    fun cancelSelected() {
        _dismissStream.postValue(Notification())
    }

}