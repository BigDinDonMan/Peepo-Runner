package com.peeporunner.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.peeporunner.ecs.comparators.ZComparator
import com.peeporunner.ecs.components.TextureComponent
import com.peeporunner.ecs.components.TransformComponent
import com.peeporunner.ecs.components.mappers.CompMappers
import java.util.*
import kotlin.collections.ArrayList


class RenderSystem(private var spriteBatch: SpriteBatch) : SortedIteratingSystem(Family.all(TransformComponent::class.java, TextureComponent::class.java).get(), ZComparator()) {

  private val entityComparator = ZComparator()
  private val queue = ArrayList<Entity>()
  var camera: OrthographicCamera = OrthographicCamera()

  init {
    this.camera.setToOrtho(false, 1024f, 720f)
  }

  override fun processEntity(entity: Entity, deltaTime: Float) { queue += entity }

  override fun update(deltaTime: Float) {
    super.update(deltaTime)

    val transformMapper = CompMappers.transformMapper
    val textureMapper = CompMappers.textureMapper

    queue.sortWith(entityComparator)

    camera.update()
    spriteBatch.projectionMatrix = camera.combined
    spriteBatch.enableBlending()
    spriteBatch.begin()

    queue.forEach {e -> run {
      val texture = textureMapper.get(e)
      val transform = transformMapper.get(e)

      val drawPositionX = transform.position.x
      val drawPositionY = transform.position.y

      val width = transform.size.x * transform.scale.x
      val height = transform.size.y * transform.scale.y

      spriteBatch.setColor(texture.color)
      spriteBatch.draw(texture.textureRegion, drawPositionX, drawPositionY, width, height)
    }}

    queue.clear()
    spriteBatch.end()
  }


}
