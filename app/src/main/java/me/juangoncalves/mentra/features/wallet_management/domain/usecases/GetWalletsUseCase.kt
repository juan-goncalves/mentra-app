package me.juangoncalves.mentra.features.wallet_management.domain.usecases

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Wallet
import me.juangoncalves.mentra.features.wallet_management.domain.repositories.WalletRepository

class GetWalletsUseCase(private val walletRepository: WalletRepository) {

    suspend fun execute(): Either<Failure, List<Wallet>> {
        return walletRepository.getWallets()
    }

}