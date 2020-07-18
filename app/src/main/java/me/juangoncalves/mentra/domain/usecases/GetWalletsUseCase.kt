package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import javax.inject.Inject

class GetWalletsUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {

    suspend operator fun invoke(): Either<Failure, List<Wallet>> = walletRepository.getWallets()

}