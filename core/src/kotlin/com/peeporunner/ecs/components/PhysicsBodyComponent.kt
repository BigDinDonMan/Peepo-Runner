package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Pool

class PhysicsBodyComponent : Component, Pool.Poolable {
    var body: Body? = null
    var freezeX = false
    var freezeY = false

    override fun reset() {
        body = null
        freezeX = false
        freezeY = false
    }
}
