package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.Left
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class GetPortfolioValueHistoryUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {

    suspend operator fun invoke(): Either<Failure, Map<LocalDate, Double>> {
        val getWalletsResult = walletRepository.getWallets()
        val wallets = getWalletsResult.rightValue ?: return Left(getWalletsResult.requireLeft())

        val valueHistories = wallets.map { wallet ->
            val getHistoryResult = walletRepository.getWalletValueHistory(wallet)
            getHistoryResult.rightValue ?: return Left(getHistoryResult.requireLeft())
        }

        // Group the values of each wallet by day
        val dateToTotalValue: TreeMap<LocalDate, Double> = TreeMap()
        valueHistories.forEach { valueHistory ->
            valueHistory.forEach { value ->
                val date = value.date.toLocalDate()
                val totalForDate = dateToTotalValue.getOrDefault(date, 0.0) + value.value
                dateToTotalValue[date] = totalForDate
            }
        }

        return Right(dateToTotalValue)
    }

}