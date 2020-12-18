package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet

interface WalletRepository {

    val wallets: Flow<List<Wallet>>

    suspend fun getWallets(): Either<Failure, List<Wallet>>

    suspend fun createWallet(wallet: Wallet): Either<Failure, Unit>

    suspend fun deleteWallet(wallet: Wallet): Either<Failure, Unit>

    suspend fun findWalletsByCoin(coin: Coin): Either<OldFailure, List<Wallet>>

    suspend fun findWalletById(id: Long): Either<OldFailure, Wallet?>

    suspend fun updateWalletValue(wallet: Wallet, price: Price): Either<OldFailure, Unit>

    suspend fun getWalletValueHistory(wallet: Wallet): Either<OldFailure, List<Price>>

    suspend fun updateWallet(wallet: Wallet, price: Price? = null): Either<OldFailure, Unit>

}