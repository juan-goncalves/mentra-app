package me.juangoncalves.mentra.features.settings.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.databinding.SettingsActivityBinding
import me.juangoncalves.mentra.extensions.animateVisibility
import me.juangoncalves.mentra.features.settings.model.SettingsViewModel

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureView()
        initObservers()
    }

    private fun configureView() {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, SettingsFragment.newInstance())
            .commit()

        binding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun initObservers() {
        viewModel.showLoadingIndicatorStream.observe(this) { shouldShow ->
            binding.progressBar.animateVisibility(shouldShow)
        }
    }

}
