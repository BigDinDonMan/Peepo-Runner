package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool


class CollisionDataComponent : Component, Pool.Poolable {

    var onCollisionEnter: (Entity) -> Unit = { }
    var onCollisionExit: (Entity) -> Unit = { }

    override fun reset() {
      onCollisionEnter = { }
      onCollisionExit = {}
    }
}
