package me.juangoncalves.mentra.ui.wallet_edit

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.usecases.wallet.UpdateWallet
import me.juangoncalves.mentra.ui.common.*
import me.juangoncalves.mentra.ui.wallet_list.DisplayWallet

class EditWalletViewModel @ViewModelInject constructor(
    private val updateWallet: UpdateWallet
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val dismiss: LiveData<Notification> get() = _dismiss
    val saveButtonEnabled: LiveData<Boolean> get() = _saveButtonEnabled
    val amountInputValidation: LiveData<Int?> get() = _amountInputValidation
    val estimatedValue: LiveData<Double> get() = _estimatedValue
    val savedUpdates: Boolean get() = _savedUpdates
    val wallet: Wallet get() = _displayWallet.wallet

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private val _amountInputValidation: MutableLiveData<Int?> = MutableLiveData(null)
    private val _saveButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _estimatedValue: MutableLiveData<Double> = MutableLiveData()

    private lateinit var _displayWallet: DisplayWallet
    private var _savedUpdates: Boolean = false
    private var _updatedAmount: Double? = null

    fun initialize(args: Bundle?) {
        _displayWallet = args?.getSerializable(BundleKeys.Wallet) as? DisplayWallet
            ?: error("You must provide the wallet to edit")

        amountInputChanged(wallet.amount.toString())
    }

    fun saveSelected() {
        val updatedAmount = _updatedAmount ?: return
        val updatedWallet = _displayWallet.wallet.copy(amount = updatedAmount)

        updateWallet.executor()
            .withDispatcher(Dispatchers.IO)
            .inScope(viewModelScope)
            .onSuccess {
                _savedUpdates = true
                _dismiss.postValue(Notification())
            }
            .onFailurePublishFleetingError()
            .run(updatedWallet)
    }

    fun cancelSelected() {
        _dismiss.postValue(Notification())
    }

    fun amountInputChanged(text: CharSequence?) {
        val (validationMessageId, parsedAmount) = validateAndParseAmountInput(text)
        _amountInputValidation.value = validationMessageId
        _saveButtonEnabled.value = validationMessageId == null && parsedAmount != wallet.amount
        _estimatedValue.value = parsedAmount * _displayWallet.currentCoinPrice
        _updatedAmount = parsedAmount
    }

    private fun validateAndParseAmountInput(text: CharSequence?): Pair<Int?, Double> {
        if (text.isNullOrEmpty()) return R.string.required_field to 0.0

        val amount = text.toString().toDoubleOrNull() ?: return R.string.invalid_number to 0.0

        return if (amount <= 0) R.string.invalid_amount_warning to 0.0 else null to amount
    }

}