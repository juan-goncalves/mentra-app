package me.juangoncalves.pie.domain

import me.juangoncalves.pie.extensions.rad


/** Divide the Pie Chart circle into a 3 by 3 grid. */
internal sealed class GridZone(val textLineDegrees: Double) {

    object TopLeft : GridZone((-135.0).rad)

    object TopMiddle : GridZone((-45.0).rad)

    object TopRight : GridZone((-45.0).rad)

    object MiddleLeft : GridZone(0.0)

    object MiddleRight : GridZone(0.0.rad)

    object BottomLeft : GridZone((-225.0).rad)

    object BottomMiddle : GridZone((-315.0).rad)

    object BottomRight : GridZone((-315.0).rad)

    object None : GridZone(0.0)

}