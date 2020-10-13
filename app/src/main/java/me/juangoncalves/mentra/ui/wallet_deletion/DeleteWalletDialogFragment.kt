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
import me.juangoncalves.mentra.databinding.DeleteWalletDialogFragmentBinding
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.extensions.showSnackbarOnDefaultErrors
import me.juangoncalves.mentra.ui.common.BundleKeys
import me.juangoncalves.mentra.ui.common.RequestKeys

@AndroidEntryPoint
class DeleteWalletDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(wallet: Wallet): DeleteWalletDialogFragment {
            val fragment = DeleteWalletDialogFragment()
            fragment.arguments = bundleOf(BundleKeys.Wallet to wallet)
            return fragment
        }
    }

    private val viewModel: DeleteWalletViewModel by viewModels()

    private var _binding: DeleteWalletDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var wallet: Wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wallet = arguments?.getSerializable(BundleKeys.Wallet) as? Wallet
            ?: error("You must provide the wallet to delete")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DeleteWalletDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObservers()
        binding.deleteButton.setOnClickListener { viewModel.onDeleteSelected(wallet) }
        binding.cancelButton.setOnClickListener { viewModel.onCancelSelected() }
    }

    private fun initObservers() {
        showSnackbarOnDefaultErrors(viewModel, binding.root)

        viewModel.dismiss.observe(viewLifecycleOwner) { notification ->
            notification.use { dismiss() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(
            RequestKeys.WalletDeletion,
            bundleOf(
                BundleKeys.Wallet to wallet,
                BundleKeys.WalletDeletionResult to viewModel.deletedWallet
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
