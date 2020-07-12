package me.juangoncalves.mentra.features.portfolio.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.clip
import androidx.ui.core.setContent
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.core.presentation.MentraApp

@AndroidEntryPoint
class PortfolioActivity : AppCompatActivity() {

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
fun PortfolioScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader {}
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
@Preview(name = "Portfolio screen")
fun PreviewPortfolioScreen() {
    MentraApp {
        PortfolioScreen()
    }
}