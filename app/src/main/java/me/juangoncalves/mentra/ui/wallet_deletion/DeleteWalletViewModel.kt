package me.juangoncalves.mentra.ui.wallet_deletion

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import me.juangoncalves.mentra.di.IoDispatcher
import me.juangoncalves.mentra.domain.usecases.wallet.DeleteWallet
import me.juangoncalves.mentra.ui.common.*
import me.juangoncalves.mentra.ui.wallet_list.DisplayWallet

class DeleteWalletViewModel @ViewModelInject constructor(
    private val deleteWallet: DeleteWallet,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val dismissStream: LiveData<Notification> get() = _dismissStream

    var walletWasDeleted: Boolean = false
        private set

    lateinit var displayWallet: DisplayWallet
        private set

    private val _dismissStream: MutableLiveData<Notification> = MutableLiveData()

    fun initialize(args: Bundle?) {
        displayWallet = args?.getSerializable(BundleKeys.Wallet) as? DisplayWallet
            ?: error("You must provide the wallet to delete")
    }

    fun deleteSelected() {
        deleteWallet.executor()
            .withDispatcher(ioDispatcher)
            .inScope(viewModelScope)
            .onSuccess {
                walletWasDeleted = true
                _dismissStream.postValue(Notification())
            }
            .onFailurePublishFleetingError()
            .run(displayWallet.wallet)
    }

    fun cancelSelected() {
        _dismissStream.postValue(Notification())
    }

}