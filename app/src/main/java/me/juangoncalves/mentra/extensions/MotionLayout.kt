package me.juangoncalves.mentra.extensions

import androidx.constraintlayout.motion.widget.MotionLayout

inline fun MotionLayout.onTransitionCompleted(
    crossinline action: (Int) -> Unit
) {
    this.addTransitionListener(object : MotionLayout.TransitionListener {

        override fun onTransitionCompleted(p0: MotionLayout?, id: Int) {
            action(id)
        }

        override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) = empty()
        override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) = empty()
        override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) =
            empty()
    })
}
