package me.juangoncalves.mentra.features.portfolio.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import me.juangoncalves.mentra.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentraApp {
                PortfolioScreen()
            }
        }
    }
}

@Composable
fun MentraApp(content: @Composable() () -> Unit) {
    MaterialTheme {
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
                Text(
                    "Clicked the button: ${counterState.value} times",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = TextUnit.Sp(20),
                        fontWeight = FontWeight.Bold
                    )
                )
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
                        0.0f to colorResource(R.color.header_gradient_start),
                        1.0f to colorResource(R.color.header_gradient_end),
                        startX = 0f,
                        endX = constraints.maxWidth.value.toFloat()
                    )
                ),
            children = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalGravity = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    content()
                }
            }
        )
    }
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
fun DefaultPreview() {
    MentraApp {
        PortfolioScreen()
    }
}