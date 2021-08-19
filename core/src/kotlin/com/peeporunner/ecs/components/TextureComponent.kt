package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

class TextureComponent : Component, Pool.Poolable {
    var textureRegion: TextureRegion? = null
    var color = Color.WHITE

    override fun reset() { textureRegion = null }
}
