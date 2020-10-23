package me.juangoncalves.pie

import me.juangoncalves.pie.extensions.toRadians

/** Divide the Pie Chart circle into a 3 by 3 grid. */
internal enum class GridZone {
    TopLeft {
        override fun textLineDegree(): Double = (-135.0).toRadians()
    },
    TopMiddle {
        override fun textLineDegree(): Double = (-135.0).toRadians()
    },
    TopRight {
        override fun textLineDegree(): Double = (-45.0).toRadians()
    },
    MiddleLeft {
        override fun textLineDegree(): Double = 0.0
    },
    MiddleRight {
        override fun textLineDegree(): Double = 0.0.toRadians()
    },
    BottomLeft {
        override fun textLineDegree(): Double = (-225.0).toRadians()
    },
    BottomMiddle {
        override fun textLineDegree(): Double = (-225.0).toRadians()
    },
    BottomRight {
        override fun textLineDegree(): Double = (-315.0).toRadians()
    },
    None {
        override fun textLineDegree(): Double = 0.0
    };

    abstract fun textLineDegree(): Double
}