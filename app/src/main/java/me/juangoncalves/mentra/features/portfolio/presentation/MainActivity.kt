package me.juangoncalves.mentra.features.portfolio.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.core.presentation.MentraTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var logger: Logger

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentraApp {
                PortfolioScreen(viewModel)
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
fun PortfolioScreen(viewModel: SplashViewModel) {
    Column(modifier = Modifier.fillMaxHeight()) {
        Column(modifier = Modifier.weight(1f, false)) {
            GradientHeader {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalGravity = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OnGradientTitle("$----.--")
                }
            }
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)
        ) {
            Factory(state = viewModel.viewState)
        }
    }
}

@Composable
fun Factory(state: SplashViewModel.State) {
    when (state) {
        is SplashViewModel.State.Loading -> Loading()
        is SplashViewModel.State.Error -> Error(state.message)
        is SplashViewModel.State.Loaded -> Text("Exito")
    }
}

@Composable
private fun Loading() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Error(message: String) {
    Text(message)
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

@Preview(name = "PortfolioScreen loading state preview")
@Composable
fun PortfolioScreenLoadingPreview() {
    MentraApp(darkTheme = false) {
        Factory(SplashViewModel.State.Loading)
    }
}

@Preview(name = "PortfolioScreen error state preview")
@Composable
fun PortfolioScreenErrorPreview() {
    MentraApp(darkTheme = false) {
        Factory(SplashViewModel.State.Error("Some random error"))
    }
}
