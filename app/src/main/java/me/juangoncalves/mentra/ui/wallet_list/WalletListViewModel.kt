package me.juangoncalves.mentra.ui.wallet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.DeleteWalletUseCase
import me.juangoncalves.mentra.domain.usecases.GetGradientCoinIconUseCase
import me.juangoncalves.mentra.domain.usecases.GetWalletsUseCase
import me.juangoncalves.mentra.domain.usecases.RefreshWalletValueUseCase
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.ui.common.DisplayError

class WalletListViewModel @ViewModelInject constructor(
    private val getWallets: GetWalletsUseCase,
    private val refreshWalletValue: RefreshWalletValueUseCase,
    private val getGradientCoinIcon: GetGradientCoinIconUseCase,
    private val deleteWallet: DeleteWalletUseCase
) : ViewModel() {

    val shouldShowProgressBar: LiveData<Boolean> get() = _shouldShowProgressBar
    val wallets: LiveData<List<DisplayWallet>> get() = _wallets
    val error: LiveData<DisplayError> get() = _error

    private val _shouldShowProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _wallets: MutableLiveData<List<DisplayWallet>> = MutableLiveData(emptyList())
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()

    init {
        refreshWallets()
    }

    fun walletCreated() = refreshWallets()

    fun deleteWalletSelected(displayWallet: DisplayWallet) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = deleteWallet(displayWallet.wallet)
            if (result.isLeft()) {
                // TODO: Show error message
            } else {
                val currentWallets = wallets.value ?: emptyList()
                val withoutRemoved = currentWallets.filter { it != displayWallet }
                _wallets.postValue(withoutRemoved)
            }
        }
    }

    private fun refreshWallets() {
        viewModelScope.launch(Dispatchers.IO) {
            _shouldShowProgressBar.postValue(true)

            val getWalletsResult = getWallets()
            val wallets = getWalletsResult.rightValue ?: return@launch displayErrorState()

            _wallets.postValue(placeholdersFor(wallets))

            val displayWallets = wallets.map { wallet ->
                val refreshResult = refreshWalletValue(wallet)
                // TODO: Handle failure appropriately
                val walletValue = refreshResult.rightValue?.value ?: -1.0
                val coinPrice = walletValue / wallet.amount
                val iconUrl = getGradientCoinIcon(wallet.coin)
                DisplayWallet(wallet, iconUrl, coinPrice, walletValue)
            }

            _wallets.postValue(displayWallets)
            _shouldShowProgressBar.postValue(false)
        }
    }

    private fun displayErrorState() {
        val error = DisplayError(R.string.default_error, ::refreshWallets)
        _error.postValue(error)
        _shouldShowProgressBar.postValue(false)
    }

    private fun placeholdersFor(wallets: List<Wallet>): List<DisplayWallet> {
        return wallets.map { wallet ->
            DisplayWallet(wallet, getGradientCoinIcon(wallet.coin), -1.0, -1.0)
        }
    }

}
