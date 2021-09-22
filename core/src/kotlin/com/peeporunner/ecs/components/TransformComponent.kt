package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class TransformComponent : Component, Pool.Poolable {
    val position = Vector3()
    val originalPosition = Vector3()
    val scale = Vector2(1f, 1f)
    val rotation = Vector2(0f, 0f)
    val size = Vector2(0f, 0f)

    override fun reset() {
        position.set(0f, 0f, 0f)
        scale.set(1f, 1f)
        rotation.set(0f, 0f)
        size.set(0f, 0f)
        originalPosition.set(0f, 0f, 0f)
        positionChanged = { _, _, _, _,_,_ -> }
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        val oldX = position.x
        val oldY = position.y
        val oldZ = position.z
        this.position.set(x,y,z)
        positionChanged.invoke(oldX, oldY, oldZ, x, y, z)
    }

    fun setPosition(position: Vector3) {
        val oldX = this.position.x
        val oldY = this.position.y
        val oldZ = this.position.z
        this.position.set(position)
        positionChanged.invoke(oldX, oldY, oldZ, this.position.x, this.position.y, this.position.z)
    }

    fun setPosition(position: Vector2) {
        val oldX = this.position.x
        val oldY = this.position.y
        val oldZ = this.position.z
        this.position.set(position.x, position.y, 0f)
        positionChanged.invoke(oldX, oldY, oldZ, position.x, position.y, 0f)
    }

    var positionChanged: (Float, Float, Float, Float, Float, Float) -> Unit = { _,_,_,_,_,_ -> }
}
