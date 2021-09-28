package com.peeporunner.ecs.components.gamelogic.bonuses

import com.peeporunner.ecs.components.gamelogic.PeepoPlayerComponent
import com.peeporunner.util.Action

enum class TemporaryBonus {
    POINT_MULTIPLIER,
    INVINCIBILITY;

//    abstract fun apply(playerComponent: PeepoPlayerComponent)
//    abstract fun unapply(playerComponent: PeepoPlayerComponent)

    val onApply: ArrayList<Action> = ArrayList()
    val onUnapply: ArrayList<Action> = ArrayList()
}