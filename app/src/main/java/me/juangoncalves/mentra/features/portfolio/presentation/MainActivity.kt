package me.juangoncalves.mentra.features.portfolio.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.lifecycle.Observer
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.core.extensions.TAG
import me.juangoncalves.mentra.core.log.Logger
import me.juangoncalves.mentra.core.presentation.MentraTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var logger: Logger

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentraApp {
                PortfolioScreen()
            }
        }

        splashViewModel.stateLiveData.observe(this, Observer {
            when (it) {
                is SplashViewModel.State.Loading -> logger.info(TAG, "Loading...")
                is SplashViewModel.State.Error -> logger.error(TAG, "Error loading coins")
                is SplashViewModel.State.Loaded -> logger.info(TAG, "Finished loading coins")
            }
        })
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
fun PortfolioScreen() {
    val counterState = state { 0 }
    Column(modifier = Modifier.fillMaxHeight()) {
        Column(modifier = Modifier.weight(1f)) {
            GradientHeader {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalGravity = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OnGradientTitle("Clicked the button: ${counterState.value} times")
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalGravity = Alignment.CenterHorizontally
        ) {
            Counter(
                modifier = Modifier.padding(10.dp),
                count = counterState.value
            ) { newCount ->
                counterState.value = newCount
            }
        }
    }
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
                        endX = constraints.maxWidth.value.toFloat()
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
fun Counter(
    modifier: Modifier = Modifier,
    count: Int,
    updateCount: (Int) -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { updateCount(count + 1) }
    ) {
        Text("Increase counter")
    }
}

@Preview(name = "PortfolioScreen preview")
@Composable
fun PortfolioScreenPreview() {
    MentraApp(darkTheme = false) {
        PortfolioScreen()
    }
}

@Preview(name = "PortfolioScreen Dark preview")
@Composable
fun PortfolioScreenDarkPreview() {
    MentraApp(darkTheme = true) {
        PortfolioScreen()
    }
}