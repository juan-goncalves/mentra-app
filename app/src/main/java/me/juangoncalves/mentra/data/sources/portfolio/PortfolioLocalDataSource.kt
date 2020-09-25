package me.juangoncalves.mentra.data.sources.portfolio

import me.juangoncalves.mentra.db.models.PortfolioValueModel

interface PortfolioLocalDataSource {

    suspend fun getLatestValue(): PortfolioValueModel?

    suspend fun saveValue(value: PortfolioValueModel)

    suspend fun getValueHistory(): List<PortfolioValueModel>

}