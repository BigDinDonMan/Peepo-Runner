package com.peeporunner.ecs.components.gamelogic

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class EnemyComponent : Component, Pool.Poolable {
    var score = 0

    override fun reset() {
        score = 0
    }
}