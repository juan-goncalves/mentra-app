package me.juangoncalves.mentra.android_cache.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.android_cache.daos.PortfolioDao
import me.juangoncalves.mentra.android_cache.mappers.PortfolioValueMapper
import me.juangoncalves.mentra.data_layer.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.domain_layer.models.Price
import javax.inject.Inject

class RoomPortfolioDataSource @Inject constructor(
    private val portfolioDao: PortfolioDao,
    private val portfolioValueMapper: PortfolioValueMapper
) : PortfolioLocalDataSource {

    override fun getPortfolioValueStream(): Flow<Price?> = portfolioDao.getPortfolioValueStream()
        .map(portfolioValueMapper::map)

    override fun getPortfolioHistoricValuesStream(): Flow<List<Price>> =
        portfolioDao.getPortfolioHistoricValuesStream()
            .map { portfolioValueMapper.map(it).filterNotNull() }

    override suspend fun insertValue(value: Price) = withContext(Dispatchers.Default) {
        val model = portfolioValueMapper.map(value)
        portfolioDao.insertValue(model)
    }

}
