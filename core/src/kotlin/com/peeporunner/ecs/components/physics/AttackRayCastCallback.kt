package com.peeporunner.ecs.components.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.physics.box2d.World
import com.peeporunner.ecs.components.mappers.CompMappers
import com.peeporunner.util.EntityRemovalService

class AttackRayCastCallback(private val playerEntity: Entity, private val removalService: EntityRemovalService) : RayCastCallback {
    //Note: this function is not called when the ray cast didn't hit anything
    override fun reportRayFixture(fixture: Fixture?, point: Vector2?, normal: Vector2?, fraction: Float): Float {
        val hitEntity = fixture?.body?.userData as? Entity ?: return 1f
        val tagComponent = CompMappers.tagMapper.get(hitEntity)
        if (tagComponent?.tag == "Enemy") {
            //kill enemy here and add points to player
            val enemyComponent = CompMappers.enemyMapper.get(hitEntity)
            val playerComponent = CompMappers.playerComponentMapper.get(playerEntity)
            playerComponent.currentPoints += enemyComponent.score
            //mark enemy entity for removal
            removalService.mark(hitEntity)
            removalService.mark(CompMappers.physicsBodyMapper.get(hitEntity)!!.body!!)
            return 0f
        }
        return 1f //return 0f to terminate raycast, -1f to filter fixture, 1f to continue raycasting
    }
}