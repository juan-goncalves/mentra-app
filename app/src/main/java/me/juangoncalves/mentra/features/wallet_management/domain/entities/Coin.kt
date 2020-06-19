package me.juangoncalves.mentra.features.wallet_management.domain.entities

data class Coin(
    val name: String,
    val symbol: String,
    val imageUrl: String
) {

    companion object {
        val Invalid = Coin("INV", "INV", "INV")
    }

}