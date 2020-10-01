package me.juangoncalves.mentra.ui.wallet_edit

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.ui.common.BundleKeys
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Notification
import me.juangoncalves.mentra.ui.wallet_list.DisplayWallet

class WalletEditViewModel @ViewModelInject constructor() : ViewModel() {

    val dismiss: LiveData<Notification> get() = _dismiss
    val onError: LiveData<DisplayError> get() = _error
    val saveButtonEnabled: LiveData<Boolean> get() = _saveButtonEnabled
    val amountInputValidation: LiveData<Int?> get() = _amountInputValidation
    val estimatedValue: LiveData<Double> get() = _estimatedValue
    val savedUpdates: Boolean get() = _savedUpdates
    val wallet: Wallet get() = _displayWallet.wallet

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private val _amountInputValidation: MutableLiveData<Int?> = MutableLiveData(null)
    private val _saveButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _estimatedValue: MutableLiveData<Double> = MutableLiveData()

    private lateinit var _displayWallet: DisplayWallet
    private var _savedUpdates: Boolean = false

    fun initialize(args: Bundle?) {
        _displayWallet = args?.getSerializable(BundleKeys.Wallet) as? DisplayWallet
            ?: error("You must provide the wallet to edit")

        amountInputChanged(wallet.amount.toString())
    }

    fun saveSelected() {

    }

    fun cancelSelected() {
        _dismiss.postValue(Notification())
    }

    fun amountInputChanged(text: CharSequence?) {
        val validationMessage = validateAmountInput(text)
        _amountInputValidation.value = validationMessage
        _saveButtonEnabled.value = validationMessage == null
        _estimatedValue.value =
            (text.toString().toDoubleOrNull() ?: 0.0) * _displayWallet.currentCoinPrice
    }

    @StringRes
    private fun validateAmountInput(text: CharSequence?): Int? {
        val amountText = text?.toString() ?: return R.string.required_field
        val amount = amountText.toDoubleOrNull() ?: return R.string.invalid_number

        return if (amount <= 0) R.string.invalid_amount_warning else null
    }

}