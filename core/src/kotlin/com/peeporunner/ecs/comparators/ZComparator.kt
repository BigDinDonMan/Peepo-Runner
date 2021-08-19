package com.peeporunner.ecs.comparators

import com.badlogic.ashley.core.Entity
import com.peeporunner.ecs.components.mappers.CompMappers
import java.util.*

class ZComparator : Comparator<Entity> {

  override fun compare(o1: Entity?, o2: Entity?): Int {
    val z1 = CompMappers.transformMapper.get(o1).position.z
    val z2 = CompMappers.transformMapper.get(o2).position.z
    return z1.compareTo(z2)
  }
}
