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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.CoinAmountInputFragmentBinding
import me.juangoncalves.mentra.extensions.applyErrorStyle
import me.juangoncalves.mentra.extensions.empty
import me.juangoncalves.mentra.extensions.hideKeyboard
import me.juangoncalves.mentra.extensions.onDismissed
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

        initObservers()
    }

    private fun initObservers() {
        viewModel.viewStateStream.observe(viewLifecycleOwner) { state ->
            bindSelectedCoinPreview(state)
            bindErrorState(state)
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

    private fun bindErrorState(state: WalletCreationState) {
        when (state.error) {
            is WalletCreationState.Error.WalletNotCreated -> {
                if (state.error.wasDismissed) return

                Snackbar
                    .make(binding.coordinator, R.string.create_wallet_error, Snackbar.LENGTH_LONG)
                    .onDismissed { actionCode ->
                        if (actionCode != Snackbar.Callback.DISMISS_EVENT_MANUAL) {
                            state.error.dismiss()
                        }
                    }
                    .applyErrorStyle()
                    .show()
                    .also { hideKeyboard() }
            }
            else -> empty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}