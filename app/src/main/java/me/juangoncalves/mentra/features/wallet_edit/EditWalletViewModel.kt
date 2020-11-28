package me.juangoncalves.mentra.features.wallet_edit

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.usecases.wallet.UpdateWallet
import me.juangoncalves.mentra.extensions.toPrice
import me.juangoncalves.mentra.features.common.*
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState
import java.math.BigDecimal

class EditWalletViewModel @ViewModelInject constructor(
    private val updateWallet: UpdateWallet
) : ViewModel(), FleetingErrorPublisher by FleetingErrorPublisherImpl() {

    val dismissStream: LiveData<Notification> get() = _dismiss
    val saveButtonStateStream: LiveData<Boolean> get() = _saveButtonEnabled
    val amountInputValidationStream: LiveData<Int?> get() = _amountInputValidation
    val estimatedValueStream: LiveData<Price> get() = _estimatedValue

    var savedUpdates: Boolean = false
        private set

    lateinit var wallet: WalletListViewState.Wallet
        private set

    private val _dismiss: MutableLiveData<Notification> = MutableLiveData()
    private val _amountInputValidation: MutableLiveData<Int?> = MutableLiveData()
    private val _saveButtonEnabled: MutableLiveData<Boolean> = MutableLiveData()
    private val _estimatedValue: MutableLiveData<Price> = MutableLiveData()

    private var _updatedAmount: BigDecimal? = null

    fun initialize(args: Bundle?) {
        wallet = args?.getParcelable(BundleKeys.Wallet)
            ?: error("You must provide the wallet to edit")

        amountInputChanged(wallet.amountOfCoin.toString())
    }

    fun saveSelected() {
        val updatedAmount = _updatedAmount ?: return
        val params = UpdateWallet.Params(wallet.id, updatedAmount)

        updateWallet.executor()
            .inScope(viewModelScope)
            .onSuccess {
                savedUpdates = true
                _dismiss.postValue(Notification())
            }
            .onFailurePublishFleetingError()
            .run(params)
    }

    fun cancelSelected() {
        _dismiss.postValue(Notification())
    }

    fun amountInputChanged(text: CharSequence?) {
        val (validationMessageId, parsedAmount) = validateAndParseAmountInput(text)
        _amountInputValidation.value = validationMessageId
        _saveButtonEnabled.value =
            validationMessageId == null && parsedAmount != wallet.amountOfCoin
        // TODO: Use the preferred user currency
        _estimatedValue.value = (parsedAmount * wallet.coin.value.value).toPrice()
        _updatedAmount = parsedAmount
    }

    private fun validateAndParseAmountInput(text: CharSequence?): Pair<Int?, BigDecimal> {
        if (text.isNullOrEmpty()) return R.string.required_field to BigDecimal.ZERO

        val amount = text.toString().toBigDecimalOrNull()
            ?: return R.string.invalid_number to BigDecimal.ZERO

        return if (amount <= BigDecimal.ZERO) R.string.invalid_amount_warning to BigDecimal.ZERO else null to amount
    }

}