package me.juangoncalves.mentra.features.wallet_management.domain.repositories

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Wallet

interface WalletRepository {

    suspend fun getWallets(): Either<Failure, List<Wallet>>

    suspend fun createWallet(wallet: Wallet): Either<Failure, Nothing>

    suspend fun deleteWallet(wallet: Wallet): Either<Failure, Nothing>

    suspend fun updateWallet(walletToUpdate: Wallet, updates: Wallet): Either<Failure, Wallet>

}