package me.juangoncalves.mentra.android_cache.sources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.android_cache.daos.PortfolioDao
import me.juangoncalves.mentra.android_cache.mappers.PortfolioValueMapper
import me.juangoncalves.mentra.data_layer.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.domain_layer.models.Price
import javax.inject.Inject

class RoomPortfolioDataSource @Inject constructor(
    private val portfolioDao: PortfolioDao,
    private val portfolioValueMapper: PortfolioValueMapper
) : PortfolioLocalDataSource {

    override fun getPortfolioValueStream(): Flow<Price> = portfolioDao.getPortfolioValueStream()
        .filterNotNull()
        .map(portfolioValueMapper::map)

    override fun getPortfolioHistoricValuesStream(): Flow<List<Price>> =
        portfolioDao.getPortfolioHistoricValuesStream()
            .map(portfolioValueMapper::map)

    override suspend fun insertValue(value: Price) {
        val model = portfolioValueMapper.map(value)
        portfolioDao.insertValue(model)
    }

}
