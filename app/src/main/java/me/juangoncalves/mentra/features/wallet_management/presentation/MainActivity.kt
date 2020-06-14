package me.juangoncalves.mentra.features.wallet_management.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.padding
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Cover()
            }
        }
    }
}

@Composable
fun Cover() {
    Column {
        val columnModifier = Modifier.padding(16.dp)
        Column(
            modifier = columnModifier
        ) {
            val textModifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
            Text(
                modifier = textModifier,
                text = "Story #1"
            )
            Text(
                modifier = textModifier,
                text = "Story #2"
            )
            Text(
                modifier = textModifier,
                text = "Story #3"
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        Cover()
    }
}