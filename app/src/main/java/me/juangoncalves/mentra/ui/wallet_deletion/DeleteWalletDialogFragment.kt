package me.juangoncalves.mentra.ui.wallet_deletion

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.DeleteWalletConfirmationDialogFragmentBinding
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.extensions.createErrorSnackbar

@AndroidEntryPoint
class DeleteWalletDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val REQUEST_CODE = "req_delete_wallet"
        const val WALLET_KEY = "wallet_arg_key"
        const val DELETION_KEY = "wallet_deleted_key"

        fun newInstance(wallet: Wallet): DeleteWalletDialogFragment {
            val fragment = DeleteWalletDialogFragment()
            fragment.arguments = bundleOf(WALLET_KEY to wallet)
            return fragment
        }
    }

    private val viewModel: WalletDeletionViewModel by viewModels()

    private var _binding: DeleteWalletConfirmationDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var wallet: Wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wallet = arguments?.getSerializable(WALLET_KEY) as? Wallet
            ?: error("You must provide the wallet to delete")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DeleteWalletConfirmationDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObservers()
        binding.deleteButton.setOnClickListener { viewModel.onDeleteSelected(wallet) }
        binding.cancelButton.setOnClickListener { viewModel.onCancelSelected() }
    }

    private fun initObservers() {
        viewModel.dismiss.observe(viewLifecycleOwner) { notification ->
            notification.content?.run { dismiss() }
        }

        viewModel.onError.observe(viewLifecycleOwner) { error ->
            createErrorSnackbar(error).show()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(
            REQUEST_CODE,
            bundleOf(
                WALLET_KEY to wallet,
                DELETION_KEY to viewModel.deletedWallet
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
