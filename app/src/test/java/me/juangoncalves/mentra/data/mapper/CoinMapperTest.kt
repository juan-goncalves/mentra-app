package me.juangoncalves.mentra.data.mapper

import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.IconType
import me.juangoncalves.mentra.network.models.CoinSchema
import me.juangoncalves.mentra.shouldBe
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
    fun `map should return the invalid coin object if the schema does not have a symbol`() {
        // Arrange
        val schema = CoinSchema(
            id = "9823",
            imageUrl = "/hello.jpg",
            name = "NANO"
        )

        // Act
        val result = sut.map(schema)

        // Assert
        result shouldBe Coin.Invalid
    }

    @Test
    fun `map should return the invalid coin object if the schema does not have a name`() {
        // Arrange
        val schema = CoinSchema(
            id = "9823",
            imageUrl = "/hello.jpg",
            symbol = "NANO"
        )

        // Act
        val result = sut.map(schema)

        // Assert
        result shouldBe Coin.Invalid
    }

    @Test
    fun `map should return a valid coin if the schema has all the required fields`() {
        // Arrange
        val schema = CoinSchema(
            id = "99",
            symbol = "BTC",
            imageUrl = "/btc.png",
            name = "Bitcoin",
            sponsored = false,
            sortPosition = "1"
        )

        // Act
        val result = sut.map(schema)

        // Assert
        result.symbol shouldBe "BTC"
        result.name shouldBe "Bitcoin"
        result.imageUrl shouldBe "/btc.png"
    }

    @Test
    fun `map should return a valid coin model based on the coin object`() {
        // Arrange
        val coin = Coin("Bitcoin", "BTC", "http://hola.com/btc.png", IconType.Unknown)

        // Act
        val result = sut.map(coin)

        // Assert
        result.symbol shouldBe "BTC"
        result.name shouldBe "Bitcoin"
        result.imageUrl shouldBe "http://hola.com/btc.png"
    }

    @Test
    fun `map should return a valid coin based on the coin model object`() {
        // Arrange
        val model = CoinModel("BTC", "http://hola.com/btc.png", "Bitcoin")

        // Act
        val result = sut.map(model)

        // Assert
        result.symbol shouldBe "BTC"
        result.name shouldBe "Bitcoin"
        result.imageUrl shouldBe "http://hola.com/btc.png"
    }

    //region Helpers
    //endregion

}