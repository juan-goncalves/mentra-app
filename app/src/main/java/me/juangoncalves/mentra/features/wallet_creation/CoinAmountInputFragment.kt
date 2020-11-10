package me.juangoncalves.mentra.features.wallet_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.CoinAmountInputFragmentBinding
import me.juangoncalves.mentra.features.wallet_creation.models.WalletCreationState

@AndroidEntryPoint
class CoinAmountInputFragment : Fragment() {

    private val viewModel: WalletCreationViewModel by activityViewModels()

    private var _binding: CoinAmountInputFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CoinAmountInputFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            viewModel.saveSelected()
        }

        binding.amountInput.doOnTextChanged { text, _, _, _ ->
            viewModel.amountInputChanged(text)
        }

        binding.amountInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveSelected()
                return@setOnEditorActionListener true
            }

            false
        }

        initObservers()
    }

    private fun initObservers() {
        viewModel.viewStateStream.observe(viewLifecycleOwner) { state ->
            bindSelectedCoinPreview(state)
            binding.saveButton.isEnabled = state.isSaveEnabled
            binding.amountInputLayout.error = when (state.inputValidation) {
                null -> null
                else -> getString(state.inputValidation)
            }
        }
    }

    private fun bindSelectedCoinPreview(state: WalletCreationState) {
        state.selectedCoin ?: error("We need a selected coin to reach this fragment")

        Glide.with(this)
            .load(state.selectedCoin.imageUrl)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.coin_placeholder)
            .into(binding.selectedCoinImageView)

        binding.selectedCoinNameTextView.text = state.selectedCoin.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}