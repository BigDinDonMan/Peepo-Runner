package com.peeporunner

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.ScreenUtils
import com.peeporunner.ui.screens.MainMenuScreen
import com.peeporunner.util.enableTtfSupport


class PeepoRunnerGame : Game() {

    private lateinit var mainBatch: SpriteBatch
    private lateinit var uiBatch: SpriteBatch
    private lateinit var ecsEngine: Engine
    private lateinit var physicsWorld: World
    private lateinit var assetManager: AssetManager
    lateinit var mainMenuScreen: MainMenuScreen

    override fun create() {
        Box2D.init()
        assetManager = AssetManager()
        assetManager.enableTtfSupport()
        mainBatch = SpriteBatch()
        uiBatch = SpriteBatch()
        ecsEngine = PooledEngine()
        physicsWorld = World(Vector2(0f, -10f), true)
        mainMenuScreen = MainMenuScreen(this, mainBatch, uiBatch, ecsEngine, physicsWorld, assetManager)
        setScreen(mainMenuScreen)
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)
        super.render() //calls current screen's render method if screen is not null
    }

    override fun dispose() {
        mainMenuScreen.dispose()
        mainBatch.dispose()
        uiBatch.dispose()
        physicsWorld.dispose()
        screen?.dispose()
        assetManager.dispose()
    }

    fun backToMainMenu() {
        val oldScreen = getScreen()
        setScreen(mainMenuScreen.apply { updateInputProcessor() })
        oldScreen.dispose()
    }
}