package com.peeporunner.ui.widgets

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.peeporunner.ecs.components.gamelogic.PeepoPlayerComponent

class HitCounter(private val playerComponent: PeepoPlayerComponent, private val hitTexture: Texture, private val scale: Float = 1f) : HorizontalGroup() {

    private val images: List<Image>
    init {
        images=ArrayList<Image>((0 until playerComponent.hits).map { Image(hitTexture) })
        images.withIndex().forEach { (index, img) -> kotlin.run {
            addActor(img)
            img.setScale(scale)
            img.setPosition(x + hitTexture.width * index * scale, y)
        } }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        for (i in 0 until playerComponent.hits) {
            images[i].draw(batch, parentAlpha)
        }
    }
}