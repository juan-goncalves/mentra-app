package me.juangoncalves.mentra.android_cache.mappers

import me.juangoncalves.mentra.android_cache.models.CoinModel
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
    fun `map should return a valid coin model based on the coin object`() {
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
    fun `map should return a valid coin based on the coin model object`() {
        // Arrange
        val model = CoinModel("BTC", "http://hola.com/btc.png", "Bitcoin", 1)

        // Act
        val result = sut.map(model)

        // Assert
        result.symbol shouldBe "BTC"
        result.name shouldBe "Bitcoin"
        result.imageUrl shouldBe "http://hola.com/btc.png"
        result.position shouldBe model.position
    }

    //region Helpers
    //endregion

}