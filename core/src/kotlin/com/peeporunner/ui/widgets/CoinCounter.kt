package com.peeporunner.ui.widgets

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import java.text.NumberFormat

class CoinCounter(coinTexture: Texture) : HorizontalGroup(), Disposable {

    private val coinsLabel: Label
    private val font = BitmapFont()
    private val coinImage: Image

    init {
        val style = Label.LabelStyle()
        style.font = font
        this.coinsLabel = Label("0", style)
        this.coinImage = Image(coinTexture)
        addActor(coinsLabel)
        addActor(coinImage)
        space(10f)
    }

    fun updateCounter(coins: Int) {
        coinsLabel.setText(NumberFormat.getIntegerInstance().format(coins))
    }

    override fun dispose() {
        font.dispose()
    }
}