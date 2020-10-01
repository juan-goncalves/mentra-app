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
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.extensions.createErrorSnackbar
import me.juangoncalves.mentra.ui.common.BundleKeys
import me.juangoncalves.mentra.ui.common.RequestKeys

@AndroidEntryPoint
class EditWalletDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(wallet: Wallet): EditWalletDialogFragment {
            val fragment = EditWalletDialogFragment()
            fragment.arguments = bundleOf(BundleKeys.Wallet to wallet)
            return fragment
        }
    }

    private val viewModel: WalletEditViewModel by viewModels()

    private var _binding: EditWalletDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var wallet: Wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wallet = arguments?.getSerializable(BundleKeys.Wallet) as? Wallet
            ?: error("You must provide the wallet to edit")
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
        binding.amountEditText.setText(wallet.amount.toString())
        binding.saveButton.setOnClickListener { viewModel.onEditSelected(wallet) }
        binding.cancelButton.setOnClickListener { viewModel.onCancelSelected() }
        binding.amountEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onAmountInputChanged(text)
        }
    }

    private fun initObservers() {
        viewModel.dismiss.observe(viewLifecycleOwner) { notification ->
            notification.use { dismiss() }
        }

        viewModel.onError.observe(viewLifecycleOwner) { error ->
            createErrorSnackbar(error).show()
        }

        viewModel.amountInputValidation.observe(viewLifecycleOwner) { messageId ->
            binding.amountInputLayout.error = if (messageId != null) getString(messageId) else null
        }

        viewModel.saveButtonEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.saveButton.isEnabled = enabled
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(
            RequestKeys.WalletEdit,
            bundleOf(
                BundleKeys.Wallet to wallet,
                BundleKeys.WalletEditResult to viewModel.savedUpdates
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
