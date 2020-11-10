package me.juangoncalves.mentra.features.wallet_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.CoinSelectionFragmentBinding
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.extensions.animateVisibility

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
        adjustFadingEdgeLayoutPadding()
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

        binding.coinNameInput.addTextChangedListener { text ->
            viewModel.submitQuery(text.toString())
        }

        initObservers()
    }

    private fun initObservers() {
        viewModel.viewStateStream.observe(viewLifecycleOwner) { state ->
            coinAdapter.data = state.coins
            binding.coinsProgressBar.animateVisibility(state.isLoadingCoins)
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

    /** Adds enough padding to make sure that it doesn;t draw below the navigation bar */
    private fun adjustFadingEdgeLayoutPadding() {
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            if (insets.systemWindowInsetBottom != 0) {
                binding.fadingEdgeLayout.updatePadding(
                    bottom = insets.systemWindowInsetBottom
                )
            }
            insets
        }
    }

}