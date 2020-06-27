package me.juangoncalves.mentra.features.portfolio.domain.usecases

import either.Either
import me.juangoncalves.mentra.core.errors.Failure
import me.juangoncalves.mentra.features.portfolio.domain.entities.Wallet
import me.juangoncalves.mentra.features.portfolio.domain.repositories.WalletRepository

class GetWalletsUseCase(private val walletRepository: WalletRepository) {

    suspend fun execute(): Either<Failure, List<Wallet>> = walletRepository.getWallets()

}