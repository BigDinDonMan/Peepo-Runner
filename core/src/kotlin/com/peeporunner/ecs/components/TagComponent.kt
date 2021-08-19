package com.peeporunner.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class TagComponent : Component, Pool.Poolable {
    var tag: String = ""

    override fun reset() { tag = "" }
}
