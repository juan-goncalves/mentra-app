package me.juangoncalves.mentra.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.SplashActivityBinding
import me.juangoncalves.mentra.ui.splash.SplashViewModel.State
import me.juangoncalves.mentra.ui.wallet_list.ClassicActivity

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    private lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.viewState.observe(this) { state ->
            when (state) {
                State.Loading -> {
                }
                is State.Error -> {
                    // TODO: Show Snackbar with retry option
                    Toast.makeText(this, R.string.default_error, Toast.LENGTH_LONG)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.eventChannel.receiveAsFlow().collect { processEvent(it) }
        }
    }

    private fun processEvent(event: SplashViewModel.Event) {
        when (event) {
            is SplashViewModel.Event.NavigateToPortfolio -> {
                val intent = Intent(this, ClassicActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                this.finish()
            }
        }
    }

}