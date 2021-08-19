package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.joints.MouseJoint
import com.badlogic.gdx.utils.Pool

class SineMovementComponent : Component, Pool.Poolable {
    var frequency = 0f
    var amplitude = 0f
    var time = 0f

    override fun reset() {
        frequency = 0f
        amplitude = 0f
        time = 0f
    }
}
