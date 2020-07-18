package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import javax.inject.Inject

class WalletRepositoryMock @Inject constructor() : WalletRepository {

    override suspend fun getWallets(): Either<Failure, List<Wallet>> {
        return Either.Right(
            listOf(
                Wallet(
                    1,
                    "Bitcoin savings",
                    Coin(
                        "Bitcoin",
                        "BTC",
                        "https://cryptoicons.org/api/icon/btc/200"
                    ),
                    32.3456
                ),
                Wallet(
                    2,
                    "Spendable",
                    Coin(
                        "Ethereum",
                        "ETH",
                        "https://cryptoicons.org/api/icon/eth/200"
                    ),
                    0.5562
                ),
                Wallet(
                    3,
                    "Spendable",
                    Coin(
                        "Nano",
                        "NANO",
                        "https://cryptoicons.org/api/icon/nano/200"
                    ),
                    55.0562
                )
            )
        )
    }

    override suspend fun createWallet(wallet: Wallet): Either<Failure, Nothing> {
        TODO("not implemented")
    }

    override suspend fun deleteWallet(wallet: Wallet): Either<Failure, Nothing> {
        TODO("not implemented")
    }

    override suspend fun updateWallet(
        walletToUpdate: Wallet,
        updates: Wallet
    ): Either<Failure, Wallet> {
        TODO("not implemented")
    }
}