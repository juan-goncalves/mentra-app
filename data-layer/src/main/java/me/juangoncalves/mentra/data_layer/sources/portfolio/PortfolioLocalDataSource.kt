package me.juangoncalves.mentra.data_layer.sources.portfolio

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.models.Price

interface PortfolioLocalDataSource {

    fun getPortfolioValueStream(): Flow<Price?>

    fun getPortfolioHistoricValuesStream(): Flow<List<Price>>

    suspend fun insertValue(value: Price)

}