package me.juangoncalves.mentra.ui.wallet_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.WalletListFragmentBinding
import me.juangoncalves.mentra.extensions.animateVisibility
import me.juangoncalves.mentra.extensions.createErrorSnackbar
import me.juangoncalves.mentra.extensions.onDismissed
import me.juangoncalves.mentra.extensions.showSnackbarOnFleetingErrors
import me.juangoncalves.mentra.ui.common.BundleKeys
import me.juangoncalves.mentra.ui.common.RequestKeys
import me.juangoncalves.mentra.ui.wallet_creation.WalletCreationActivity
import me.juangoncalves.mentra.ui.wallet_deletion.DeleteWalletDialogFragment
import me.juangoncalves.mentra.ui.wallet_edit.EditWalletDialogFragment

@AndroidEntryPoint
class WalletListFragment : Fragment(), WalletSwipeHelper.Listener {

    private val viewModel: WalletListViewModel by viewModels()
    private val walletAdapter: WalletAdapter = WalletAdapter(emptyList())

    private var _binding: WalletListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(RequestKeys.WalletDeletion) { _, bundle ->
            processWalletActionResult(bundle, BundleKeys.WalletDeletionResult)
        }

        setFragmentResultListener(RequestKeys.WalletEdit) { _, bundle ->
            processWalletActionResult(bundle, BundleKeys.WalletEditResult)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WalletListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = walletAdapter
            val touchHelper = WalletTouchHelper(requireContext(), this@WalletListFragment)
            touchHelper.attachToRecyclerView(this)
        }

        binding.addWalletButton.setOnClickListener {
            val intent = Intent(requireContext(), WalletCreationActivity::class.java)
            startActivity(intent)
        }
        
        initObservers()
    }

    private fun initObservers() {
        showSnackbarOnFleetingErrors(viewModel, binding.addWalletButton)

        viewModel.shouldShowProgressBar.observe(viewLifecycleOwner) { shouldShow ->
            binding.progressBar.animateVisibility(shouldShow)
        }

        viewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletAdapter.data = wallets
        }

        viewModel.walletManagementError.observe(viewLifecycleOwner) { (error, position) ->
            createErrorSnackbar(error, binding.addWalletButton)
                .onDismissed { walletAdapter.notifyItemChanged(position) }
                .show()
        }
    }

    override fun onDeleteWalletGesture(position: Int) {
        DeleteWalletDialogFragment
            .newInstance(walletAdapter.data[position])
            .show(parentFragmentManager, "delete_wallet")
    }

    override fun onEditWalletGesture(position: Int) {
        EditWalletDialogFragment
            .newInstance(walletAdapter.data[position])
            .show(parentFragmentManager, "delete_wallet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Resets the recycler view item swipe position if the action was cancelled.
     *
     * For example: when the user swipes to the left to delete a wallet and then
     * he dismisses the dialog, we have to reset the item back to its original
     * state (e.g hide the delete bubble).
     */
    private fun processWalletActionResult(bundle: Bundle, resultKey: String) {
        val wallet = bundle[BundleKeys.Wallet] as? DisplayWallet
        val wasModified = bundle.getBoolean(resultKey, false)
        if (wasModified) return

        val position = walletAdapter.data.indexOfFirst { it == wallet }
        if (position > -1) walletAdapter.notifyItemChanged(position)
    }

}
