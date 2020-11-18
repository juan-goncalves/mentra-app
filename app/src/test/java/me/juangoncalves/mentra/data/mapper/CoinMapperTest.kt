package me.juangoncalves.mentra.data.mapper

import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.IconType
import me.juangoncalves.mentra.network.models.CoinSchema
import org.junit.Assert.assertEquals
import org.junit.Test

class CoinMapperTest {

    private val sut: CoinMapper = CoinMapper()

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
        assertEquals(Coin.Invalid, result)
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
        assertEquals(Coin.Invalid, result)
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
        assertEquals("BTC", result.symbol)
        assertEquals("Bitcoin", result.name)
        assertEquals("/btc.png", result.imageUrl)
    }

    @Test
    fun `map should return a valid coin model based on the coin object`() {
        // Arrange
        val coin = Coin("Bitcoin", "BTC", "http://hola.com/btc.png", IconType.Unknown)

        // Act
        val result = sut.map(coin)

        // Assert
        assertEquals("BTC", result.symbol)
        assertEquals("Bitcoin", result.name)
        assertEquals("http://hola.com/btc.png", result.imageUrl)
    }

    @Test
    fun `map should return a valid coin based on the coin model object`() {
        // Arrange
        val model = CoinModel("BTC", "http://hola.com/btc.png", "Bitcoin")

        // Act
        val result = sut.map(model)

        // Assert
        assertEquals("BTC", result.symbol)
        assertEquals("Bitcoin", result.name)
        assertEquals("http://hola.com/btc.png", result.imageUrl)
    }

}