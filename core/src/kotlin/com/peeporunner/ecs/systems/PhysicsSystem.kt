package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import com.peeporunner.ecs.components.PhysicsBodyComponent
import com.peeporunner.ecs.components.TransformComponent
import com.peeporunner.ecs.components.mappers.CompMappers

class PhysicsSystem(private val world: World) : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsBodyComponent::class.java).get()) {

    private val queue = ArrayList<Entity>()
    private var accumulator = 0f
    private val TIME_STEP = 1/300f

    override fun processEntity(entity: Entity, deltaTime: Float) { queue += entity }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        accumulator += deltaTime
        while (accumulator >= deltaTime) {
            world.step(TIME_STEP, 8, 4)
            accumulator -= TIME_STEP
        }

        queue.forEach { e ->
            run {
                val transform = CompMappers.transformMapper.get(e)
                val body = CompMappers.physicsBodyMapper.get(e)
                val bodyPosition = body.body!!.position
                var x = transform.position.x
                var y = transform.position.y
                if (!body.freezeX)
                    x = bodyPosition.x - transform.size.x / 2 * transform.scale.x
                if (!body.freezeY)
                    y = bodyPosition.y - transform.size.y / 2 * transform.scale.y
                transform.setPosition(x, y, transform.position.z)
            }
        }

        queue.clear()
    }
}
