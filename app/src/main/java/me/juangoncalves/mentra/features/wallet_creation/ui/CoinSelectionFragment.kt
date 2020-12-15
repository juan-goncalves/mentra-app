package me.juangoncalves.mentra.features.wallet_creation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.CoinSelectionFragmentBinding
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.extensions.animateVisibility
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationViewModel

@AndroidEntryPoint
class CoinSelectionFragment : Fragment(), CoinAdapter.Listener {

    private val viewModel: WalletCreationViewModel by activityViewModels()
    private val coinAdapter: CoinAdapter = CoinAdapter(this)

    private var _binding: CoinSelectionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CoinSelectionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewManager = GridLayoutManager(requireContext(), 3)
        binding.coinSelectionList.apply {
            layoutManager = viewManager
            adapter = coinAdapter
            setHasFixedSize(true)
        }

        binding.retryButton.setOnClickListener {
            viewModel.retryLoadCoinListSelected()
        }

        binding.coinNameInput.addTextChangedListener { text ->
            viewModel.submitQuery(text.toString())
        }

        initObservers()
    }

    private fun initObservers() {
        viewModel.coinListStream.observe(viewLifecycleOwner) { coins ->
            coinAdapter.data = coins
        }

        viewModel.isLoadingCoinListStream.observe(viewLifecycleOwner) { shouldShow ->
            binding.coinsProgressBar.animateVisibility(shouldShow, 300L)
        }

        viewModel.errorStream.observe(viewLifecycleOwner) { error ->
            bindErrorState(error)
        }

        viewModel.shouldShowNoMatchesWarningStream.observe(viewLifecycleOwner) { shouldShow ->
            binding.noSearchResultsTextView.animateVisibility(shouldShow, 300L)
        }
    }

    private fun bindErrorState(error: WalletCreationViewModel.Error) {
        when (error) {
            is WalletCreationViewModel.Error.CoinsNotLoaded -> {
                binding.coinNameInput.isEnabled = false
                binding.errorStateView.animateVisibility(true, 300L)
            }
            is WalletCreationViewModel.Error.None -> {
                binding.coinNameInput.isEnabled = true
                binding.errorStateView.animateVisibility(false, 300L)
            }
        }
    }

    override fun onCoinSelected(coin: Coin) {
        viewModel.selectCoin(coin)
    }

    override fun onCommitCoinListUpdates() {
        binding.coinSelectionList.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}