package me.juangoncalves.mentra.features.wallet_edit

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.EditWalletDialogFragmentBinding
import me.juangoncalves.mentra.extensions.asCurrencyAmount
import me.juangoncalves.mentra.failures.FailureHandlingDialogFragment
import me.juangoncalves.mentra.features.common.BundleKeys
import me.juangoncalves.mentra.features.common.RequestKeys
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState

@AndroidEntryPoint
class EditWalletDialogFragment : FailureHandlingDialogFragment<EditWalletViewModel>() {

    companion object {
        fun newInstance(wallet: WalletListViewState.Wallet): EditWalletDialogFragment {
            val fragment = EditWalletDialogFragment()
            fragment.arguments = bundleOf(BundleKeys.Wallet to wallet)
            return fragment
        }
    }

    override val viewModel: EditWalletViewModel get() = _viewModel
    private val _viewModel: EditWalletViewModel by viewModels()

    private val binding get() = _binding!!
    private var _binding: EditWalletDialogFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditWalletDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun configureView() {
        binding.amountEditText.setText(viewModel.wallet.amountOfCoin.toString())
        binding.saveButton.setOnClickListener { viewModel.saveSelected() }
        binding.cancelButton.setOnClickListener { viewModel.cancelSelected() }
        binding.amountEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.amountInputChanged(text)
        }
    }

    private fun initObservers() {
        viewModel.dismissStream.observe(viewLifecycleOwner) { notification ->
            notification.use { dismiss() }
        }

        viewModel.amountInputValidationStream.observe(viewLifecycleOwner) { messageId ->
            binding.amountInputLayout.error = if (messageId != null) getString(messageId) else null
        }

        viewModel.saveButtonStateStream.observe(viewLifecycleOwner) { enabled ->
            binding.saveButton.isEnabled = enabled
        }

        viewModel.estimatedValueStream.observe(viewLifecycleOwner) { value ->
            binding.priceTextView.text = value.asCurrencyAmount()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(
            RequestKeys.WalletEdit,
            bundleOf(
                BundleKeys.Wallet to viewModel.wallet,
                BundleKeys.WalletEditResult to viewModel.savedUpdates
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
