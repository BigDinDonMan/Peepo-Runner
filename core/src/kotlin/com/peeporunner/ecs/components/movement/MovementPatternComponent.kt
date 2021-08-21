package com.peeporunner.ecs.components.movement

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

abstract class MovementPatternComponent : Component, Pool.Poolable {
    abstract fun calculate(velocity: Vector2) // modify the vector in place
}