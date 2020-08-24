package me.juangoncalves.mentra.ui.wallet_list

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
import me.juangoncalves.mentra.ui.add_wallet.WalletFormFragment

@AndroidEntryPoint
class WalletListFragment : Fragment() {

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
        binding.recyclerView.apply {
            layoutManager = viewManager
            adapter = walletAdapter
        }
        binding.addWalletButton.setOnClickListener {
            WalletFormFragment().show(parentFragmentManager, "create_wallet")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}