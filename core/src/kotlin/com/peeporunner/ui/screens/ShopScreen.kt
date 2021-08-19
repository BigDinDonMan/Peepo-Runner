package com.peeporunner.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.peeporunner.GameGlobals
import com.peeporunner.PeepoRunnerGame
import com.peeporunner.ui.widgets.CoinCounter
import com.peeporunner.util.addActors

class ShopScreen(val game: PeepoRunnerGame, val assetManager: AssetManager) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private val stage = Stage(viewport)
    private val coinCounter by lazy { CoinCounter(assetManager.get("graphics/pepecoin-platinum.png", Texture::class.java)) }

    init {
        loadShopAssets()
        Gdx.input.inputProcessor = stage
        coinCounter.align(Align.topRight)
        coinCounter.updateCounter(GameGlobals.coins())
        coinCounter.setPosition(Gdx.graphics.width.toFloat() - coinCounter.width, Gdx.graphics.height.toFloat() - coinCounter.height)
        val returnButton = TextButton("Go back", Skin(Gdx.files.internal("skins/uiskin.json")))
        returnButton.align(Align.topLeft)
        returnButton.setPosition(0f, Gdx.graphics.height - returnButton.height)
        returnButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) = game.backToMainMenu()
        })
        stage.addActors(coinCounter, returnButton)
    }

    private fun loadShopAssets() {
        assetManager.load("graphics/pepecoin-platinum.png", Texture::class.java)
        assetManager.finishLoading()
    }

    override fun dispose() {
        coinCounter.dispose()
        stage.dispose()
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)
}