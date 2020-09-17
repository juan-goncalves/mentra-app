package me.juangoncalves.mentra.domain.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet

interface WalletRepository {

    suspend fun getWallets(): Either<Failure, List<Wallet>>

    suspend fun createWallet(wallet: Wallet): Either<Failure, Unit>

    suspend fun findWalletsByCoin(coin: Coin): Either<Failure, List<Wallet>>

    suspend fun updateWalletValue(wallet: Wallet, price: Price): Either<Failure, Unit>

    suspend fun getWalletValueHistory(wallet: Wallet): Either<Failure, List<Price>>

}