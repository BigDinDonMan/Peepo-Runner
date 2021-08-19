package com.peeporunner.ecs.components.gamelogic

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class CoinComponent : Component, Pool.Poolable {
    var coinType: CoinType? = null

    override fun reset() { coinType = null }
}
