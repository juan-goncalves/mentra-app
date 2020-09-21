package me.juangoncalves.mentra.ui.wallet_list

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.WalletListFragmentBinding
import me.juangoncalves.mentra.extensions.animateVisibility
import me.juangoncalves.mentra.extensions.createErrorSnackbar
import me.juangoncalves.mentra.ui.wallet_creation.WalletCreationActivity

@AndroidEntryPoint
class WalletListFragment : Fragment() {

    companion object {
        private const val CREATE_WALLET = 1001
    }

    private val viewModel: WalletListViewModel by viewModels()
    private val walletAdapter: WalletAdapter = WalletAdapter(emptyList())

    private var _binding: WalletListFragmentBinding? = null
    private val binding get() = _binding!!

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
        val viewManager = LinearLayoutManager(context)
        val swipeHandler = SwipeToDeleteHelper(requireContext(), ::onDeleteItemAtPosition)
        val touchHelper = ItemTouchHelper(swipeHandler)

        binding.recyclerView.apply {
            layoutManager = viewManager
            adapter = walletAdapter
            touchHelper.attachToRecyclerView(this)
        }

        binding.addWalletButton.setOnClickListener {
            // TODO: Refactor to VM to VM communication
            val intent = Intent(requireContext(), WalletCreationActivity::class.java)
            startActivityForResult(intent, CREATE_WALLET)
        }

        initObservers()
    }

    private fun initObservers() {
        viewModel.shouldShowProgressBar.observe(viewLifecycleOwner) { shouldShow ->
            binding.progressBar.animateVisibility(shouldShow)
        }

        viewModel.wallets.observe(viewLifecycleOwner) { wallets ->
            walletAdapter.data = wallets
        }

        viewModel.generalError.observe(viewLifecycleOwner) { error ->
            createErrorSnackbar(error, binding.addWalletButton).show()
        }

        viewModel.walletManagementError.observe(viewLifecycleOwner) { (error, position) ->
            createErrorSnackbar(error, binding.addWalletButton)
                .addCallback(DismissCallback(position))
                .show()
        }
    }

    private fun onDeleteItemAtPosition(position: Int) {
        DeleteWalletConfirmationDialogFragment(
            onCancel = { walletAdapter.notifyItemChanged(position) },
            onConfirm = { viewModel.deleteWalletSelected(position) }
        ).show(parentFragmentManager, "delete_wallet")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREATE_WALLET && resultCode == RESULT_OK) {
            viewModel.walletCreated()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class DismissCallback(
        val itemPosition: Int
    ) : BaseTransientBottomBar.BaseCallback<Snackbar>() {

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            if (event != DISMISS_EVENT_ACTION) walletAdapter.notifyItemChanged(itemPosition)
            transientBottomBar?.removeCallback(this)
        }

    }

}