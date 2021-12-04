package me.juangoncalves.mentra.features.wallet_deletion

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.common.BundleKeys
import me.juangoncalves.mentra.common.Notification
import me.juangoncalves.mentra.domain_layer.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.failures.FailurePublisher
import me.juangoncalves.mentra.failures.GeneralFailurePublisher
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import javax.inject.Inject

@HiltViewModel
class DeleteWalletViewModel @Inject constructor(
    private val deleteWallet: DeleteWallet
) : ViewModel(), FailurePublisher by GeneralFailurePublisher() {

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