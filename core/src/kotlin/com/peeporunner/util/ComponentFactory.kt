package com.peeporunner.util

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Audio
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import com.peeporunner.ecs.components.*
import com.peeporunner.ecs.components.gamelogic.CoinComponent
import com.peeporunner.ecs.components.gamelogic.CoinType
import com.peeporunner.ecs.components.gamelogic.EnemyComponent
import java.util.*

class ComponentFactory(val engine: Engine, val world: World) {
    fun newTransform(initialX: Float, initialY: Float, width: Float, height: Float, scaleX: Float = 1f, scaleY: Float = 1f, zIndex: Float = 0f): TransformComponent =
        engine.createComponent(TransformComponent::class.java).apply {
            position.set(initialX, initialY, zIndex)
            scale.set(scaleX, scaleY)
            size.set(width, height)
        }

    fun newCircleBody(type: BodyDef.BodyType = BodyDef.BodyType.StaticBody, initialX: Float = 0f, initialY: Float = 0f,
                      linearDamping: Float = 0f, angularDamping: Float = 0f, gravityScale: Float = 1f, radius: Float = 1f,
                      userData: Any? = null, allowSleep: Boolean = true, density: Float = 1f,
                      fixedRotation: Boolean = true, active: Boolean = true, friction: Float = 0f,
                      isBullet: Boolean = false, awake: Boolean = true, restitution: Float = 0f,
                      isSensor: Boolean = false): PhysicsBodyComponent
    {
        return createFromShape(
                CircleShape().apply { setRadius(radius) }, type, initialX, initialY,
                linearDamping, angularDamping, gravityScale, userData,
                allowSleep, density, fixedRotation, active,
                friction, isBullet, awake, restitution, isSensor
        )
    }

    fun newBoxBody(type: BodyDef.BodyType = BodyDef.BodyType.StaticBody, initialX: Float = 0f, initialY: Float = 0f,
                   linearDamping: Float = 0f, angularDamping: Float = 0f, gravityScale: Float = 1f, boxWidth: Float = 1f,
                   boxHeight: Float = 1f, userData: Any? = null, allowSleep: Boolean = true,
                   density: Float = 1f, fixedRotation: Boolean = true, active: Boolean = true,
                   friction: Float = 0f, isBullet: Boolean = false, awake: Boolean = true,
                   restitution: Float = 0f, isSensor: Boolean = false): PhysicsBodyComponent
    {
        return createFromShape(
                PolygonShape().apply { setAsBox(boxWidth / 2, boxHeight / 2) }, type, initialX, initialY,
                linearDamping, angularDamping, gravityScale, userData,
                allowSleep, density, fixedRotation, active,
                friction, isBullet, awake, restitution, isSensor
        )
    }

    private fun createFromShape(shape: Shape, type: BodyDef.BodyType = BodyDef.BodyType.StaticBody, initialX: Float = 0f, initialY: Float = 0f,
                                linearDamping: Float = 0f, angularDamping: Float = 0f, gravityScale: Float = 1f,
                                userData: Any? = null, allowSleep: Boolean = true, density: Float = 1f,
                                fixedRotation: Boolean = true, active: Boolean = true, friction: Float = 0f,
                                isBullet: Boolean = false, awake: Boolean = true, restitution: Float = 0f,
                                isSensor: Boolean = false): PhysicsBodyComponent
    {
        val bodyDefinition = BodyDef().apply {
            this.type = type
            this.position.set(initialX, initialY)
            this.gravityScale = gravityScale
            this.fixedRotation = fixedRotation
            this.active = active
            this.bullet = isBullet
            this.linearDamping = linearDamping
            this.angularDamping = angularDamping
            this.allowSleep = allowSleep
            this.awake = awake
        }
        val body = world.createBody(bodyDefinition).apply { this.userData = userData }
        val fixtureDefinition = FixtureDef().apply {
            this.shape = shape
            this.isSensor = isSensor
            this.density = density
            this.friction = friction
            this.restitution = restitution
        }
        return engine.createComponent(PhysicsBodyComponent::class.java).apply {
            body.createFixture(fixtureDefinition)
            this.body = body
            shape.dispose()
        }
    }

    fun newAnimator(vararg animationData: Pair<String, AnimationComponent.AnimationStateData>): AnimationComponent {
        return engine.createComponent(AnimationComponent::class.java).apply {
            animationData.forEach { (key, data) -> animations[key] = data }
        }
    }

    fun newTag(tag: String): TagComponent = engine.createComponent(TagComponent::class.java).apply { this.tag = tag }

    fun newCollisionHandler(onCollisionEnter: CollisionHandler = {}, onCollisionExit: CollisionHandler = {}): CollisionDataComponent =
            engine.createComponent(CollisionDataComponent::class.java).apply {
                this.onCollisionEnter = onCollisionEnter
                this.onCollisionExit = onCollisionExit
            }

    fun newVelocityData(initialVelocityX: Float = 0f, initialVelocityY: Float = 0f): EnvironmentVelocityComponent =
            engine.createComponent(EnvironmentVelocityComponent::class.java).apply { velocity.set(initialVelocityX, initialVelocityY) }

    fun newSineWaveData(amplitude: Float, frequency: Float): SineMovementComponent =
            engine.createComponent(SineMovementComponent::class.java).apply {
                this.amplitude = amplitude
                this.frequency = frequency
            }

    fun newCoinComponent(coinType: CoinType): CoinComponent = engine.createComponent(CoinComponent::class.java).apply { this.coinType = coinType }

    fun newTextureComponent(texture: Texture, color: Color = Color.WHITE): TextureComponent =
            engine.createComponent(TextureComponent::class.java).apply {
                this.textureRegion = TextureRegion(texture)
                this.color = color
            }

    fun newAudioComponent(vararg sounds: Pair<String, Sound>): AudioComponent = engine.createComponent(AudioComponent::class.java).apply {
        this.sounds.putAll(sounds)
    }

    fun newEnemyData(score: Int): EnemyComponent = engine.createComponent(EnemyComponent::class.java).apply {
        this.score = score
    }
}