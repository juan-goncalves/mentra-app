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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.WalletListFragmentBinding
import me.juangoncalves.mentra.extensions.animateVisibility
import me.juangoncalves.mentra.ui.wallet_actions.WalletActionsDialogFragment
import me.juangoncalves.mentra.ui.wallet_creation.WalletCreationActivity

@AndroidEntryPoint
class WalletListFragment : Fragment() {

    companion object {
        private const val CREATE_WALLET = 1001
    }

    private val viewModel: WalletListViewModel by viewModels()
    private val walletAdapter: WalletAdapter = WalletAdapter(emptyList(), ::onWalletSelected)

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

        binding.recyclerView.apply {
            layoutManager = viewManager
            adapter = walletAdapter
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

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Snackbar.make(binding.root, error.messageId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { error.retryAction() }
                .show()
        }
    }

    private fun onWalletSelected(wallet: DisplayWallet) {
        WalletActionsDialogFragment().show(parentFragmentManager, "wallet_actions")
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
}