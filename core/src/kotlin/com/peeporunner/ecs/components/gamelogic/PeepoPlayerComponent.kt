package com.peeporunner.ecs.components.gamelogic

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PeepoPlayerComponent : Component, Pool.Poolable {
    var jumpCount = 0
    var canJump = false
    var playerJumpForce = 0f
    var currentPlayerCoins = 0
    var currentPoints = 0
    var hits = 3
    var range = 250f
    var attackCooldown = 0.4f // seconds
    var hitCooldown = 0.75f
    var isHit = false

    override fun reset() {
        canJump = false
        jumpCount = 0
        playerJumpForce = 0f
        currentPlayerCoins = 0
        currentPoints = 0
        hits = 3
        range = 250f
        attackCooldown = 0.4f
        hitCooldown = 0.75f
    }
}
