package me.juangoncalves.mentra.ui.wallet_list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.ui.common.MentraApp
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
        Spacer(modifier = Modifier.height(12.dp))
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
            modifier = Modifier.fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 20.dp, horizontal = 12.dp)
        ) {
            // TODO: Replace with image loaded from URL
            Box(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                backgroundColor = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(16.dp))
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
    )
)

@Composable
@Preview(name = "Wallet list screen")
fun PreviewWalletListScreen() {

    MentraApp(darkTheme = true) {
        WalletListScreen(MutableLiveData(fakeWallets))
    }
}