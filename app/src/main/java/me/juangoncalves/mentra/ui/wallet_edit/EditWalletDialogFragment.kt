package me.juangoncalves.mentra.ui.wallet_edit

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.EditWalletDialogFragmentBinding
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.extensions.showSnackbarOnDefaultErrors
import me.juangoncalves.mentra.ui.common.BundleKeys
import me.juangoncalves.mentra.ui.common.RequestKeys
import me.juangoncalves.mentra.ui.wallet_list.DisplayWallet

@AndroidEntryPoint
class EditWalletDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(wallet: DisplayWallet): EditWalletDialogFragment {
            val fragment = EditWalletDialogFragment()
            fragment.arguments = bundleOf(BundleKeys.Wallet to wallet)
            return fragment
        }
    }

    private val viewModel: EditWalletViewModel by viewModels()

    private var _binding: EditWalletDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize(arguments)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditWalletDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObservers()
        binding.amountEditText.setText(viewModel.wallet.amount.toString())
        binding.saveButton.setOnClickListener { viewModel.saveSelected() }
        binding.cancelButton.setOnClickListener { viewModel.cancelSelected() }
        binding.amountEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.amountInputChanged(text)
        }
    }

    private fun initObservers() {
        showSnackbarOnDefaultErrors(viewModel, binding.root)

        viewModel.dismiss.observe(viewLifecycleOwner) { notification ->
            notification.use { dismiss() }
        }

        viewModel.amountInputValidation.observe(viewLifecycleOwner) { messageId ->
            binding.amountInputLayout.error = if (messageId != null) getString(messageId) else null
        }

        viewModel.saveButtonEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.saveButton.isEnabled = enabled
        }

        viewModel.estimatedValue.observe(viewLifecycleOwner) { value ->
            binding.priceTextView.text = value.asCurrency(symbol = "$")
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
