package me.juangoncalves.mentra.features.wallet_deletion

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.DeleteWalletDialogFragmentBinding
import me.juangoncalves.mentra.failures.FailureHandlingDialogFragment
import me.juangoncalves.mentra.features.common.BundleKeys
import me.juangoncalves.mentra.features.common.RequestKeys
import me.juangoncalves.mentra.features.wallet_list.models.WalletListViewState

@AndroidEntryPoint
class DeleteWalletDialogFragment : FailureHandlingDialogFragment<DeleteWalletViewModel>() {

    companion object {
        fun newInstance(wallet: WalletListViewState.Wallet): DeleteWalletDialogFragment {
            val fragment = DeleteWalletDialogFragment()
            fragment.arguments = bundleOf(BundleKeys.Wallet to wallet)
            return fragment
        }
    }

    override val viewModel: DeleteWalletViewModel get() = _viewModel
    private val _viewModel: DeleteWalletViewModel by viewModels()

    private var _binding: DeleteWalletDialogFragmentBinding? = null
    private val binding get() = _binding!!

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
        _binding = DeleteWalletDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun configureView() {
        binding.deleteButton.setOnClickListener { viewModel.deleteSelected() }
        binding.cancelButton.setOnClickListener { viewModel.cancelSelected() }
    }

    private fun initObservers() {
        viewModel.dismissStream.observe(viewLifecycleOwner) { notification ->
            notification.use { dismiss() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(
            RequestKeys.WalletDeletion,
            bundleOf(
                BundleKeys.Wallet to viewModel.wallet,
                BundleKeys.WalletDeletionResult to viewModel.walletWasDeleted
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
