package me.juangoncalves.mentra.data.sources.portfolio

import me.juangoncalves.mentra.db.daos.PortfolioDao
import me.juangoncalves.mentra.db.models.PortfolioValueModel
import javax.inject.Inject

class PortfolioLocalDataSourceImpl @Inject constructor(
    private val portfolioDao: PortfolioDao
) : PortfolioLocalDataSource {

    override suspend fun getLatestValue(): PortfolioValueModel? {
        return portfolioDao.getLatestPortfolioValue()
    }

    override suspend fun saveValue(value: PortfolioValueModel) {
        return portfolioDao.insertValue(value)
    }

    override suspend fun getValueHistory(): List<PortfolioValueModel> {
        return portfolioDao.getValueHistory()
    }

}