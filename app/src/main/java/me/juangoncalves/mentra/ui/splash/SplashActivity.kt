package me.juangoncalves.mentra.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.databinding.SplashActivityBinding
import me.juangoncalves.mentra.extensions.animateVisibility
import me.juangoncalves.mentra.ui.dashboard.DashboardActivity

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    private lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObservers()
    }

    private fun initObservers() {
        viewModel.error.observe(this) { error ->
            Snackbar.make(binding.root, error.messageId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { error.retryAction() }
                .show()
        }

        viewModel.shouldShowProgressBar.observe(this) { shouldShow ->
            binding.progressBar.animateVisibility(shouldShow)
        }

        viewModel.navigateToDashboard.observe(this) { event ->
            event.content?.run { launchDashboardActivity() }
        }
    }

    private fun launchDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        this.finish()
    }

}