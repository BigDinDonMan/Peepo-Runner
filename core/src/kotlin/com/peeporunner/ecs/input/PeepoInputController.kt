package com.peeporunner.ecs.input

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.physics.box2d.World
import com.peeporunner.ecs.components.mappers.CompMappers
import com.peeporunner.ui.screens.PeepoGameScreen


class PeepoInputController(private val world: World, private val playerEntity: Entity, private val gameScreen: PeepoGameScreen) : InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameScreen.togglePause()
        }
        return jump { Gdx.input.isKeyJustPressed(Input.Keys.SPACE) }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return jump { Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isTouched(pointer) }
    }

    private fun jump(buttonPressPredicate: () -> Boolean): Boolean {
        val playerComponent = CompMappers.playerComponentMapper.get(playerEntity)
        return if (buttonPressPredicate.invoke() && playerComponent.canJump) {
            playerComponent.jumpCount++
            playerComponent.canJump = playerComponent.jumpCount < 2
            val physicsBodyComponent = CompMappers.physicsBodyMapper.get(playerEntity)
            val body = physicsBodyComponent.body
            val center = body!!.worldCenter
            val impulseForce = playerComponent.playerJumpForce * body.mass / playerComponent.jumpCount //second jump should apply less force so we divide it by jump count
            body.applyLinearImpulse(0f, impulseForce, center.x, center.y, true)
            true
        } else false
    }
}
