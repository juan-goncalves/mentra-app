package me.juangoncalves.mentra.domain_layer.usecases.wallet

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.FlowUseCase
import javax.inject.Inject

class GetWalletListStream @Inject constructor(
    private val walletRepository: WalletRepository
) : FlowUseCase<List<Wallet>> {

    override operator fun invoke(): Flow<List<Wallet>> = walletRepository.wallets

}