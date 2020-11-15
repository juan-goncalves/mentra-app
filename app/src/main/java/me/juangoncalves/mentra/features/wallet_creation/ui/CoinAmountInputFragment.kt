package me.juangoncalves.mentra.features.wallet_creation.ui

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
import me.juangoncalves.mentra.extensions.hideKeyboard
import me.juangoncalves.mentra.extensions.showSnackbarOnFleetingErrors
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
    ): View? {
        _binding = CoinAmountInputFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindSelectedCoinPreview()
        initObservers()

        binding.saveButton.setOnClickListener {
            viewModel.saveSelected()
            hideKeyboard()
        }

        binding.amountInput.doOnTextChanged { text, _, _, _ ->
            viewModel.amountInputChanged(text)
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
        showSnackbarOnFleetingErrors(viewModel, view = binding.coordinator)

        viewModel.isSaveActionEnabledStream.observe(viewLifecycleOwner) { isEnabled ->
            binding.saveButton.isEnabled = isEnabled
        }

        viewModel.amountInputValidationStream.observe(viewLifecycleOwner) { stringId ->
            binding.amountInputLayout.error = when (stringId) {
                null -> null
                else -> getString(stringId)
            }
        }
    }

    private fun bindSelectedCoinPreview() {
        val selectedCoin = viewModel.selectedCoin
            ?: error("We need a selected coin to reach this fragment")

        Glide.with(this)
            .load(selectedCoin.imageUrl)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.coin_placeholder)
            .into(binding.selectedCoinImageView)

        binding.selectedCoinNameTextView.text = selectedCoin.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}