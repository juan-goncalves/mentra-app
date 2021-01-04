package me.juangoncalves.mentra.features.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import me.juangoncalves.mentra.databinding.OnboardingCurrencyFragmentBinding


class OnboardingCurrencyFragment : Fragment() {

    private val viewModel: OnboardingViewModel by activityViewModels()

    private var _binding: OnboardingCurrencyFragmentBinding? = null
    private val binding get() = _binding!!

    private var position: Int = 0
    private val currencyAdapter = CurrencyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OnboardingCurrencyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureView()
        initObservers()
    }

    private fun configureView() = with(binding) {
        currencyRecyclerView.apply {
            adapter = currencyAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        previousStepButton.setOnClickListener { viewModel.scrolledToStep(position - 1) }
    }

    private fun initObservers() = with(viewModel) {
        currenciesStream.observe(viewLifecycleOwner) { currencies ->
            currencyAdapter.data = currencies
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int) = OnboardingCurrencyFragment().apply {
            arguments = bundleOf("position" to position)
        }
    }

}