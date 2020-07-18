package me.juangoncalves.mentra.ui.wallet_list

import android.os.Bundle
import androidx.activity.viewModels
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
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.extensions.asCoinAmount
import me.juangoncalves.mentra.extensions.asCurrency
import me.juangoncalves.mentra.ui.common.MentraApp
import me.juangoncalves.mentra.ui.common.NetworkImage
import me.juangoncalves.mentra.ui.portfolio.GradientHeader
import me.juangoncalves.mentra.ui.wallet_list.WalletListViewModel.State
import java.util.*

@AndroidEntryPoint
class WalletListActivity : AppCompatActivity() {

    private val viewModel: WalletListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentraApp {
                WalletListScreen(viewModel.viewState)
            }
        }
    }

}

@Composable
private fun WalletListScreen(viewStateLiveData: LiveData<State>) {
    val viewState by viewStateLiveData.observeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader {}
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.wallets_subtitle).toUpperCase(Locale.getDefault()),
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(modifier = Modifier.height(12.dp))
        when (val safeState = viewState) {
            is State.Loading -> Loading(!safeState.hasLoadedData)
            is State.Error -> Text(stringResource(safeState.messageId))
            is State.Loaded -> WalletList(safeState.wallets)
        }
    }
}

@Composable
private fun Loading(shouldShow: Boolean) {
    if (!shouldShow) return

    Box(
        modifier = Modifier.fillMaxSize(),
        gravity = ContentGravity.TopCenter
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun WalletList(wallets: List<DisplayWallet>) {
    LazyColumnItems(
        items = wallets,
        modifier = Modifier.fillMaxSize()
    ) { wallet ->
        Wallet(wallet)
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun Wallet(displayWallet: DisplayWallet) {
    val wallet = displayWallet.wallet
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
                        text = "$ ${displayWallet.currentCoinPrice.asCurrency()}",
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
                        text = "$ ${displayWallet.currentWalletPrice.asCurrency()}",
                        style = MaterialTheme.typography.subtitle1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                                text = wallet.amount.asCoinAmount(),
                                style = MaterialTheme.typography.caption,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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


@Composable
@Preview(name = "Loaded - Wallet list screen")
fun PreviewWalletListScreenLoadedState() {
    val fakeWallets = listOf(
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
    val fakeDisplays = fakeWallets.map {
        DisplayWallet(it, 123.32, 2341.31, emptyList())
    }

    MentraApp(darkTheme = true) {
        WalletListScreen(MutableLiveData(State.Loaded(fakeDisplays)))
    }
}

@Composable
@Preview(name = "Loading - Wallet list screen")
fun PreviewWalletListScreenLoadingState() {
    MentraApp(darkTheme = true) {
        WalletListScreen(MutableLiveData(State.Loading()))
    }
}

@Composable
@Preview(name = "Error - Wallet list screen")
fun PreviewWalletListScreenErrorState() {
    MentraApp(darkTheme = true) {
        WalletListScreen(MutableLiveData(State.Error(R.string.default_error)))
    }
}