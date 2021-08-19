package com.peeporunner.ui.widgets

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle

class AnimatedImage(private val animation: Animation<TextureRegion>) : Image(animation.getKeyFrame(0f)) {

    private var stateTime = 0f
    private var flipX = 1f
    private var flipY = 1f

    override fun act(delta: Float) {
        stateTime += delta
        (drawable as TextureRegionDrawable).region = animation.getKeyFrame(stateTime, true)
        super.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val drawable = drawable
        val c = color
        batch.setColor(c.r, c.g, c.b, c.a * parentAlpha)
        if (drawable != null) {
            var x = x
            var y = y
            var width = width
            var height = height
            if (flipX < 0f) {
                x += width
                width *= flipX
            }
            if (flipY < 0f) {
                y += height
                height *= flipY
            }
            batch.draw((drawable as TextureRegionDrawable).region, x, y, width, height)
        }
    }

    fun flipX() {
        flipX *= -1f
    }

    fun flipY() {
        flipY *= -1f
    }

    fun flipAxes() {
        flipX()
        flipY()
    }
}