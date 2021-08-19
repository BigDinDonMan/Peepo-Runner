package com.peeporunner.ui.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.peeporunner.PeepoRunnerGame
import com.peeporunner.ui.widgets.AnimatedImage
import com.peeporunner.util.addActors
import kotlin.system.exitProcess

class MainMenuScreen(val game: PeepoRunnerGame, val spriteBatch: SpriteBatch, private val uiBatch: SpriteBatch, val engine: Engine, val world: World, val assetManager: AssetManager) : ScreenAdapter(), ApplicationListener {

    private var elapsed = 0f
    private val viewport = StretchViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private val stage = Stage(viewport, uiBatch)
    private val font = BitmapFont()
    private val titleFont by lazy { assetManager.get("fonts/cartoonish.ttf", BitmapFont::class.java) }
    private val peepoRunAtlas by lazy { assetManager.get("graphics/peeporun.atlas", TextureAtlas::class.java) }
    private val peepoAnimation by lazy { Animation<TextureRegion>(0.05f, peepoRunAtlas.regions) }

    init {
        loadMainMenuAssets()
        updateInputProcessor()
        val vbox = VerticalGroup()
        stage.addActor(vbox)
        vbox.setFillParent(true)

        val lowerLeftImage = AnimatedImage(peepoAnimation)
        val upperLeftImage = AnimatedImage(peepoAnimation)
        upperLeftImage.flipY()
        val lowerRightImage = AnimatedImage(peepoAnimation)
        lowerRightImage.flipX()
        val upperRightImage = AnimatedImage(peepoAnimation)
        upperRightImage.flipAxes()
        listOf(lowerLeftImage, lowerRightImage, upperRightImage, upperLeftImage).forEach(stage::addActor)
        val screenHeight = Gdx.graphics.height
        val screenWidth = Gdx.graphics.width
        lowerLeftImage.setPosition(0f, 0f)
        upperLeftImage.setPosition(0f, screenHeight - upperLeftImage.height)
        lowerRightImage.setPosition(screenWidth - lowerRightImage.width, 0f)
        upperRightImage.setPosition(screenWidth - upperRightImage.width, screenHeight - upperRightImage.height)

        val labelStyle = Label.LabelStyle(titleFont, Color.WHITE)
        val title = Label("Peepo Run", labelStyle)

        val style = TextButton.TextButtonStyle()
        style.font = font
        val startButton = TextButton("Start running", style)
        startButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = PeepoGameScreen(game, spriteBatch, uiBatch, engine, world, assetManager)
            }
        })
        val exitButton = TextButton("Exit", style)
        exitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                Gdx.app.exit()
                exitProcess(0)
            }
        })
        val creditsButton = TextButton("Credits", style)
        creditsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {

            }
        })
        val shopButton = TextButton("Peepo shop", style)
        shopButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = ShopScreen(game, assetManager)
            }
        })
        vbox.padTop(60f).addActors(title, startButton, shopButton, creditsButton, exitButton)
    }

    private fun loadMainMenuAssets() {
        assetManager.load("graphics/peeporun.atlas", TextureAtlas::class.java)
        assetManager.load("fonts/cartoonish.ttf", BitmapFont::class.java, FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
            fontFileName = "fonts/cartoonish.ttf"
            fontParameters.size = 116 //go big or go home
        })
        assetManager.finishLoading()
    }

    fun updateInputProcessor() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        elapsed += delta
        stage.act(delta)
        stage.draw()
    }

    override fun create() {}

    override fun resize(width: Int, height: Int) { viewport.update(width, height) }

    override fun dispose() {
        stage.dispose()
        font.dispose()
    }

    override fun render() = this.render(Gdx.graphics.deltaTime)
}
