package me.juangoncalves.mentra.android_cache.mappers

import me.juangoncalves.mentra.android_cache.entities.CoinEntity
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.test_utils.shouldBe
import org.junit.Before
import org.junit.Test

class CoinMapperTest {

    //region Rules
    //endregion

    //region Mocks
    //endregion

    private lateinit var sut: CoinMapper

    @Before
    fun setUp() {
        sut = CoinMapper()
    }

    @Test
    fun `map should return a valid entity based on the domain object`() {
        // Arrange
        val coin = Coin("Bitcoin", "BTC", "http://hola.com/btc.png", 1)

        // Act
        val result = sut.map(coin)

        // Assert
        result.symbol shouldBe "BTC"
        result.name shouldBe "Bitcoin"
        result.imageUrl shouldBe "http://hola.com/btc.png"
        result.position shouldBe 1
    }

    @Test
    fun `map should return a valid domain coin based on the entity`() {
        // Arrange
        val entity = CoinEntity("BTC", "http://hola.com/btc.png", "Bitcoin", 1)

        // Act
        val result = sut.map(entity)

        // Assert
        result.symbol shouldBe "BTC"
        result.name shouldBe "Bitcoin"
        result.imageUrl shouldBe "http://hola.com/btc.png"
        result.position shouldBe entity.position
    }

    //region Helpers
    //endregion
}