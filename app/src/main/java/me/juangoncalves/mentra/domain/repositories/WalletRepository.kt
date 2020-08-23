package me.juangoncalves.mentra.domain.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Wallet

interface WalletRepository {

    suspend fun getWallets(): Either<Failure, List<Wallet>>

    suspend fun createWallet(wallet: Wallet): Either<Failure, Unit>

}