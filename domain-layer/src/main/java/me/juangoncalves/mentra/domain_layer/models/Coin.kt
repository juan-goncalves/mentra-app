package me.juangoncalves.mentra.domain_layer.models

data class Coin(
    val name: String,
    val symbol: String,
    val imageUrl: String,
    val iconType: IconType = IconType.Unknown,
    val position: Int,
) {

    companion object {
        val Invalid = Coin("INV", "INV", "INV", IconType.Unknown, Int.MAX_VALUE)
    }

}