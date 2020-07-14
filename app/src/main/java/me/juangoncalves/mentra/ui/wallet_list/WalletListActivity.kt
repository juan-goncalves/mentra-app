package me.juangoncalves.mentra.ui.wallet_list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.VerticalGradient
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.ui.common.MentraApp
import me.juangoncalves.mentra.ui.common.NetworkImage
import me.juangoncalves.mentra.ui.portfolio.GradientHeader
import java.util.*

@AndroidEntryPoint
class WalletListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentraApp {
                WalletListScreen(MutableLiveData(fakeWallets))
            }
        }
    }

}

@Composable
fun WalletListScreen(walletsLiveData: LiveData<List<Wallet>>) {
    val wallets by walletsLiveData.observeAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader {}
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.wallets_subtitle).toUpperCase(Locale.getDefault()),
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumnItems(
            items = wallets ?: emptyList(),
            modifier = Modifier.fillMaxSize()
        ) { wallet ->
            Wallet(wallet)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun Wallet(wallet: Wallet) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            Row(
                modifier = Modifier.weight(3.75f)
                    .fillMaxHeight()
                    .padding(vertical = 20.dp, horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape
                ) {
                    NetworkImage(url = wallet.coin.imageUrl) {
                        CoinPlaceholder()
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = wallet.coin.symbol,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$ 6448.06",
                        style = MaterialTheme.typography.caption
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalGravity = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$ 1322.32",
                        style = MaterialTheme.typography.subtitle1
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    TextWithIcon(
                        icon = {
                            Icon(
                                modifier = Modifier.size(14.dp),
                                asset = vectorResource(R.drawable.ic_coins),
                                tint = MaterialTheme.typography.caption.color
                            )
                        },
                        text = {
                            Text(
                                text = "0.0342",
                                style = MaterialTheme.typography.caption
                            )
                        }
                    )
                }
            }
            Row(modifier = Modifier.weight(1.25f).fillMaxHeight()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = Color(0xFF, 0x36, 0x81, 0x36),
                    gravity = ContentGravity.BottomStart
                ) {
                    Text("chart")
                }
            }
        }
    }
}

@Composable
private fun TextWithIcon(
    icon: @Composable() () -> Unit,
    text: @Composable() () -> Unit
) {
    Row(verticalGravity = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(4.dp))
        text()
    }
}

@Composable
private fun CoinPlaceholder() {
    WithConstraints {
        Box(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            border = Border(2.dp, Color.Gray)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .clip(CircleShape)
                    .drawBackground(
                        VerticalGradient(
                            0.0f to Color.LightGray,
                            1.0f to Color.Gray,
                            startY = 0f,
                            endY = constraints.maxWidth.toFloat()
                        )
                    )
            )
        }
    }
}


private val fakeWallets = listOf(
    Wallet(
        1,
        "Bitcoin savings",
        Coin("Bitcoin", "BTC", "https://cryptoicons.org/api/icon/btc/200"),
        0.3456
    ),
    Wallet(
        2,
        "Spendable",
        Coin("Ethereum", "ETH", "https://cryptoicons.org/api/icon/eth/200"),
        0.0562
    ),
    Wallet(
        2,
        "Spendable",
        Coin("FAKE", "FAKE", "https://wjhefw.com/jid.sj"),
        0.0562
    )
)

@Composable
@Preview(name = "Wallet list screen")
fun PreviewWalletListScreen() {
    MentraApp(darkTheme = true) {
        WalletListScreen(MutableLiveData(fakeWallets))
    }
}