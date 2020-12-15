package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.WalletCreationFailure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet

interface WalletRepository {

    val wallets: Flow<List<Wallet>>

    suspend fun getWallets(): Either<Failure, List<Wallet>>

    /**
     * Saves the wallet in a persistent storage.
     * Returns [WalletCreationFailure] if the wallet could not be saved.
     * */
    suspend fun createWallet(wallet: Wallet): Either<Failure, Unit>

    suspend fun deleteWallet(wallet: Wallet): Either<Failure, Unit>

    suspend fun findWalletsByCoin(coin: Coin): Either<Failure, List<Wallet>>

    suspend fun findWalletById(id: Long): Either<Failure, Wallet?>

    suspend fun updateWalletValue(wallet: Wallet, price: Price): Either<Failure, Unit>

    suspend fun getWalletValueHistory(wallet: Wallet): Either<Failure, List<Price>>

    suspend fun updateWallet(wallet: Wallet, price: Price? = null): Either<Failure, Unit>

}