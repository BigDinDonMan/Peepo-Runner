package com.peeporunner.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class EntityPool(initialSize: Int, maxSize: Int) : Pool<Entity>(initialSize, maxSize) {

    override fun newObject(): Entity = Entity()

    override fun reset(`object`: Entity?) { `object`?.removeAll() }
}
