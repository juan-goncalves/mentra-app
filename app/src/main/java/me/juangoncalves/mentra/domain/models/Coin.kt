package me.juangoncalves.mentra.domain.models

data class Coin(
    val name: String,
    val symbol: String,
    val imageUrl: String
) {

    companion object {
        val Invalid = Coin("INV", "INV", "INV")
    }

}