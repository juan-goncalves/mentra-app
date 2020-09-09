package me.juangoncalves.mentra.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.StatsFragmentBinding

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private val viewModel: StatsViewModel by viewModels()

    private var _binding: StatsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}