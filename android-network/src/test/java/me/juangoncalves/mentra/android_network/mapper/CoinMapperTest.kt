package me.juangoncalves.mentra.android_network.mapper

import me.juangoncalves.mentra.android_network.services.crypto_compare.models.CoinSchema
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.test_utils.shouldBe
import org.junit.Before
import org.junit.Test

class CoinMapperTest {

    private lateinit var sut: CoinMapper

    @Before
    fun setUp() {
        sut = CoinMapper()
    }

    @Test
    fun `map should return an invalid coin if it doesn't have a name`() {
        // Arrange
        val schema = CoinSchema(
            id = "234324",
            symbol = "BTC",
            imageUrl = "https://sample.com/btc.jpg",
            name = "",
            sponsored = false,
            sortPosition = "1",
        )

        // Act
        val result = sut.map(schema)

        // Assert
        result shouldBe Coin.Invalid
    }

    @Test
    fun `map should return an invalid coin if it doesn't have a symbol`() {
        // Arrange
        val schema = CoinSchema(
            id = "234324",
            symbol = "",
            imageUrl = "https://sample.com/btc.jpg",
            name = "Bitcoin",
            sponsored = false,
            sortPosition = "1",
        )

        // Act
        val result = sut.map(schema)

        // Assert
        result shouldBe Coin.Invalid
    }

    @Test
    fun `map should return an invalid coin if it doesn't have a valid sort position`() {
        // Arrange
        val schema = CoinSchema(
            id = "234324",
            symbol = "BTC",
            imageUrl = "https://sample.com/btc.jpg",
            name = "Bitcoin",
            sponsored = false,
            sortPosition = "Not a number",
        )

        // Act
        val result = sut.map(schema)

        // Assert
        result shouldBe Coin.Invalid
    }

    @Test
    fun `map should return a valid coin based on the schema`() {
        // Arrange
        val schema = CoinSchema(
            id = "234324",
            symbol = "BTC",
            imageUrl = "https://sample.com/btc.jpg",
            name = "Bitcoin",
            sponsored = false,
            sortPosition = "1",
        )

        // Act
        val result = sut.map(schema)

        // Assert
        val expected = Coin(
            name = "Bitcoin",
            symbol = "BTC",
            imageUrl = "https://sample.com/btc.jpg",
            position = 1,
        )

        result shouldBe expected
    }
}