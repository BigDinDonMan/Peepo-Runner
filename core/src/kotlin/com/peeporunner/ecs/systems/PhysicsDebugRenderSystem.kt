package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World

class PhysicsDebugRenderSystem(private val world: World, private val camera: OrthographicCamera) : IteratingSystem(Family.all().get()) {

    private val renderer = Box2DDebugRenderer()

    override fun processEntity(entity: Entity?, deltaTime: Float) {}

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        renderer.render(world, camera.combined)
    }
}
