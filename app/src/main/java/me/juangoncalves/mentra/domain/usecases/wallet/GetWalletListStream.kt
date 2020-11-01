package me.juangoncalves.mentra.domain.usecases.wallet

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import javax.inject.Inject

class GetWalletListStream @Inject constructor(
    private val walletRepository: WalletRepository
) {

    operator fun invoke(): Flow<List<Wallet>> = walletRepository.wallets

}