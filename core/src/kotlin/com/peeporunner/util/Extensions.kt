package com.peeporunner.util

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage

fun Texture.resize(newWidth: Int, newHeight: Int): Texture {
    this.textureData.prepare()
    val srcPixmap = this.textureData.consumePixmap()
    val destPixmap = Pixmap(newWidth, newHeight, srcPixmap.format)
    destPixmap.drawPixmap(srcPixmap, 0, 0, srcPixmap.width, srcPixmap.height, 0, 0, newWidth, newHeight)
    val texture = Texture(destPixmap)
    destPixmap.dispose()
    return texture
}

fun Texture.resize(scaleX: Float, scaleY: Float): Texture {
    return this.resize((this.width * scaleX).toInt(), (this.height * scaleY).toInt())
}

fun Boolean.toInt(): Int = if (this) 1 else 0

fun Entity.addComponents(vararg components: Component) {
    components.forEach(this::add)
}

fun Stage.addActors(vararg actors: Actor) {
    this.root.addActors(*actors)
}

fun Group.addActors(vararg actors: Actor) {
    actors.forEach(this::addActor)
}

fun AssetManager.enableTtfSupport() {
    val resolver = InternalFileHandleResolver()
    this.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
    this.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
}