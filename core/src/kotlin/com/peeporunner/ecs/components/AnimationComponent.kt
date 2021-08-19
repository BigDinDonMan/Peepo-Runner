package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import com.peeporunner.util.AnimationSet
import java.util.*

class AnimationComponent : Component, Pool.Poolable {

    class AnimationStateData(val animation: Animation<TextureRegion>) {
        private var callbackCalled = false
        var animationFinished = {}
        var animTime = 0f

        fun currentFrame(): TextureRegion = animation.getKeyFrame(animTime)
        fun isAnimationFinished(): Boolean = animation.isAnimationFinished(animTime)

        fun onAnimationFinished() {
            if (callbackCalled) return
            callbackCalled = true
            animationFinished.invoke()
            animTime = 0f
        }

        fun refreshCallback() {
            callbackCalled = false
        }
    }

    val animations = AnimationSet()
    var currentAnimKey = ""

    fun changeAnimation(key: String) {
        if (animations.containsKey(key)) {
            currentAnimKey = key
            animations[key]!!.animTime = 0f
        }
    }

    fun currentState(): AnimationStateData = animations[currentAnimKey]!!
    fun currentFrame(): TextureRegion = currentState().currentFrame()
    fun isAnimationFinished(): Boolean = currentState().isAnimationFinished()



    override fun reset() {
        currentAnimKey = ""
        animations.clear()
    }
}
