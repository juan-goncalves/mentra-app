package me.juangoncalves.mentra.features.wallet_creation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.Coil
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.CoinAmountInputFragmentBinding
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.extensions.*
import me.juangoncalves.mentra.features.wallet_creation.model.WalletCreationViewModel

@AndroidEntryPoint
class CoinAmountInputFragment : Fragment() {

    private val viewModel: WalletCreationViewModel by activityViewModels()

    private var _binding: CoinAmountInputFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CoinAmountInputFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    private fun configureView() {
        binding.saveButton.setOnClickListener {
            viewModel.saveSelected()
            hideKeyboard()
        }

        binding.amountInput.apply {
            requestFocus()
            showKeyboard()
            doOnTextChanged { text, _, _, _ ->
                viewModel.amountInputChanged(text)
            }
        }

        binding.amountInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveSelected()
                hideKeyboard()
                return@setOnEditorActionListener true
            }

            false
        }
    }

    private fun initObservers() {
        handleErrorsFrom(viewModel)

        viewModel.selectedCoinStream.observe(viewLifecycleOwner) { selectedCoin ->
            bindSelectedCoinPreview(selectedCoin)
        }

        viewModel.isSaveActionEnabledStream.observe(viewLifecycleOwner) { isEnabled ->
            binding.saveButton.isEnabled = isEnabled
        }

        viewModel.amountInputValidationStream.observe(viewLifecycleOwner) { validation ->
            binding.amountInputLayout.error = when (validation) {
                WalletCreationViewModel.Validation.None -> null
                else -> getString(validation.messageId)
            }
        }

        viewModel.shouldShowSaveProgressIndicatorStream.observe(viewLifecycleOwner) { shouldShow ->
            binding.saveProgressBar.animateVisibility(shouldShow, 300L)
        }
    }

    private fun bindSelectedCoinPreview(selectedCoin: Coin?) {
        selectedCoin ?: error("We need a selected coin to reach this fragment")

        val context = binding.root.context

        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .placeholder(R.drawable.coin_placeholder)
            .data(selectedCoin.imageUrl)
            .target(binding.selectedCoinImageView)
            .transformations(CircleCropTransformation())
            .crossfade(context.resources.getInteger(android.R.integer.config_shortAnimTime))
            .build()
            .also { request -> Coil.enqueue(request) }

        binding.selectedCoinNameTextView.text = selectedCoin.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}