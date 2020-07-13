package me.juangoncalves.mentra.features.splash.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.*
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Snackbar
import androidx.ui.material.snackbarPrimaryColorFor
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.text.AnnotatedString
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.core.presentation.MentraApp
import me.juangoncalves.mentra.features.portfolio.presentation.PortfolioActivity
import me.juangoncalves.mentra.features.splash.presentation.SplashViewModel.Event
import me.juangoncalves.mentra.features.splash.presentation.SplashViewModel.State

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentraApp {
                SplashScreen(
                    viewStateLiveData = viewModel.viewState,
                    onRetryInitialization = viewModel::retryInitialization
                )
            }
        }
        lifecycleScope.launch {
            viewModel.eventChannel.receiveAsFlow().collect { processEvent(it) }
        }
    }

    private fun processEvent(event: Event) {
        when (event) {
            is Event.NavigateToPortfolio -> {
                val intent = Intent(this, PortfolioActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                this.finish()
            }
        }
    }

}

@Composable
fun SplashScreen(viewStateLiveData: LiveData<State>, onRetryInitialization: () -> Unit) {
    val viewState by viewStateLiveData.observeAsState()
    Stack(
        modifier = Modifier.fillMaxSize()
            .drawBackground(MaterialTheme.colors.surface)
    ) {
        Image(
            modifier = Modifier.size(110.dp, 110.dp).gravity(Alignment.Center),
            asset = imageResource(R.drawable.app_icon)
        )
        Column(
            modifier = Modifier.gravity(Alignment.BottomCenter).padding(bottom = 8.dp)
        ) {
            when (val safeState = viewState) {
                is State.Loading -> Box()
                is State.Error -> Error(stringResource(safeState.messageId), onRetryInitialization)
            }
        }
    }
}

@Composable
private fun Error(message: String, onRetry: () -> Unit) {
    Snackbar(
        text = { Text(message) },
        modifier = Modifier.padding(8.dp),
        action = {
            ClickableText(
                text = AnnotatedString(stringResource(R.string.retry)),
                modifier = Modifier.padding(end = 16.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = snackbarPrimaryColorFor(MaterialTheme.colors)
                ),
                onClick = { onRetry() }
            )
        }
    )
}

@Composable
@Preview(name = "Splash Screen Loading State")
fun PreviewSplashScreenLoading() {
    MentraApp(darkTheme = false) {
        SplashScreen(
            viewStateLiveData = MutableLiveData(State.Loading),
            onRetryInitialization = {}
        )
    }
}

@Composable
@Preview(name = "Splash Screen Error State")
fun PreviewSplashScreenError() {
    MentraApp(darkTheme = false) {
        SplashScreen(
            viewStateLiveData = MutableLiveData(State.Error(R.string.default_error)),
            onRetryInitialization = {}
        )
    }
}
