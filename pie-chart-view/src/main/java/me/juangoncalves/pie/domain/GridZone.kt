package me.juangoncalves.pie.domain

import me.juangoncalves.pie.extensions.toRadians

/** Divide the Pie Chart circle into a 3 by 3 grid. */
internal sealed class GridZone(val textLineDegrees: Double) {
    
    object TopLeft : GridZone((-135.0).toRadians())

    object TopMiddle : GridZone((-45.0).toRadians())

    object TopRight : GridZone((-45.0).toRadians())

    object MiddleLeft : GridZone(0.0)

    object MiddleRight : GridZone(0.0.toRadians())

    object BottomLeft : GridZone((-225.0).toRadians())

    object BottomMiddle : GridZone((-315.0).toRadians())

    object BottomRight : GridZone((-315.0).toRadians())

    object None : GridZone(0.0)

}