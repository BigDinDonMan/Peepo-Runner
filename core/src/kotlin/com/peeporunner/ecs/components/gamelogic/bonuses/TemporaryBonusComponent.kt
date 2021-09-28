package com.peeporunner.ecs.components.gamelogic.bonuses

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TemporaryBonusComponent : Component, Pool.Poolable {

    var bonus: TemporaryBonus? = null

    override fun reset() {
        bonus = null
    }
}