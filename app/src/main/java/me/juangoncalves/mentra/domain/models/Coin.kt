package me.juangoncalves.mentra.domain.models

data class Coin(
    val name: String,
    val symbol: String,
    val imageUrl: String,
    val iconType: IconType = IconType.Unknown
) {

    companion object {
        val Invalid = Coin("INV", "INV", "INV", IconType.Unknown)
    }

}