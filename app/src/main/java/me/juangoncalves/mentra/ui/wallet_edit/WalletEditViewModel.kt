package me.juangoncalves.mentra.ui.wallet_edit

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.ui.common.DisplayError
import me.juangoncalves.mentra.ui.common.Notification

class WalletEditViewModel @ViewModelInject constructor() : ViewModel() {

    val dismiss: LiveData<Notification> get() = _dismiss
    val onError: LiveData<DisplayError> get() = _error
    val saveButtonEnabled: LiveData<Boolean> get() = _saveButtonEnabled
    val amountInputValidation: LiveData<Int?> get() = _amountInputValidation
    val savedUpdates: Boolean get() = _savedUpdates

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private val _error: MutableLiveData<DisplayError> = MutableLiveData()
    private val _amountInputValidation: MutableLiveData<Int?> = MutableLiveData(null)
    private val _saveButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private var _savedUpdates: Boolean = false

    fun onEditSelected(wallet: Wallet) {

    }

    fun onCancelSelected() {
        _dismiss.postValue(Notification())
    }

    fun onAmountInputChanged(text: CharSequence?) {
        val validationMessage = validateAmountInput(text)
        _amountInputValidation.value = validationMessage
        _saveButtonEnabled.value = validationMessage == null
    }

    @StringRes
    private fun validateAmountInput(text: CharSequence?): Int? {
        val amountText = text?.toString() ?: return R.string.required_field
        val amount = amountText.toDoubleOrNull() ?: return R.string.invalid_number

        return if (amount <= 0) R.string.invalid_amount_warning else null
    }

}