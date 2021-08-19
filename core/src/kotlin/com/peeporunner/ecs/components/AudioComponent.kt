package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.peeporunner.util.AudioSet
import com.peeporunner.util.MusicSet

class AudioComponent : Component, Pool.Poolable {
    val sounds = AudioSet()

    override fun reset() {
        sounds.clear()
    }

    fun play(soundName: String, volume: Float = 1f) = sounds[soundName]?.play(volume)
}
