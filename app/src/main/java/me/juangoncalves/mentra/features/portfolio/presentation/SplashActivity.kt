package me.juangoncalves.mentra.features.portfolio.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Snackbar
import androidx.ui.material.Surface
import androidx.ui.material.snackbarPrimaryColorFor
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.text.AnnotatedString
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.core.presentation.MentraTheme
import me.juangoncalves.mentra.features.portfolio.presentation.SplashViewModel.State

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentraApp {
                SplashScreen(
                    viewStateLiveData = viewModel.viewState,
                    onRetry = viewModel::retryInitialization
                )
            }
        }
    }

}

@Composable
fun MentraApp(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    MentraTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}

@Composable
fun SplashScreen(viewStateLiveData: LiveData<State>, onRetry: () -> Unit) {
    val viewState by viewStateLiveData.observeAsState()
    Stack(
        modifier = Modifier.fillMaxSize()
            .drawBackground(MaterialTheme.colors.surface)
    ) {
        Image(
            modifier = Modifier.size(110.dp, 110.dp).gravity(Alignment.Center),
            asset = imageResource(R.drawable.cubes_gradient)
        )
        Column(
            modifier = Modifier.gravity(Alignment.BottomCenter).padding(bottom = 8.dp)
        ) {
            when (val safeState = viewState) {
                is State.Loading -> Box()
                is State.Error -> Error(safeState.message, onRetry)
                is State.Loaded -> Spacer(Modifier.size(0.dp)) // TODO: Navigate to portfolio screen
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
fun GradientHeader(content: @Composable() () -> Unit) {
    WithConstraints {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .preferredHeight(250.dp)
                .clip(
                    RoundedCornerShape(
                        bottomRight = 20.dp,
                        bottomLeft = 20.dp
                    )
                )
                .drawBackground(
                    HorizontalGradient(
                        0.0f to MaterialTheme.colors.primary,
                        1.0f to MaterialTheme.colors.primaryVariant,
                        startX = 0f,
                        endX = constraints.maxWidth.toFloat()
                    )
                ),
            children = content
        )
    }
}

@Composable
fun OnGradientTitle(text: String) {
    Text(
        text,
        style = TextStyle(
            color = Color.White,
            fontSize = TextUnit.Sp(20),
            fontWeight = FontWeight.Bold
        )
    )
}


@Composable
@Preview(name = "Splash Screen Loading State")
fun PreviewSplashScreenLoading() {
    MentraApp(darkTheme = false) {
        SplashScreen(
            viewStateLiveData = MutableLiveData(State.Loading),
            onRetry = {}
        )
    }
}

@Composable
@Preview(name = "Splash Screen Error State")
fun PreviewSplashScreenError() {
    MentraApp(darkTheme = false) {
        SplashScreen(
            viewStateLiveData = MutableLiveData(State.Error("Some error message")),
            onRetry = {}
        )
    }
}
