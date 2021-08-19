package com.peeporunner.ui.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.peeporunner.GameGlobals
import com.peeporunner.PeepoRunnerGame
import com.peeporunner.util.addActors
import java.text.NumberFormat

class GameOverScreen(val game: PeepoRunnerGame, val spriteBatch: SpriteBatch, val uiBatch: Batch, val engine: Engine, val world: World, val assetManager: AssetManager, private val gameScore: Int, private val collectedCoins: Int) : ScreenAdapter() {

    private val viewport = StretchViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    private val stage = Stage(viewport, uiBatch)
    private val highScore = GameGlobals.highScore()

    init {
        setUpGameOverUI()
    }

    private fun setUpGameOverUI() {
        Gdx.input.inputProcessor = stage
        val skin = Skin(Gdx.files.internal("skins/uiskin.json"))
        val retryButton = TextButton("Retry", skin)
        retryButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                persistGameData()
                val oldScreen = game.screen
                game.screen = PeepoGameScreen(game, spriteBatch, uiBatch as SpriteBatch, engine, world, assetManager)
                oldScreen.dispose()
            }
        })
        val exitButton = TextButton("Exit to menu", skin)
        exitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                persistGameData()
                game.mainMenuScreen.apply { updateInputProcessor() }
                game.screen = game.mainMenuScreen
                dispose()
            }
        })
        val integerInstance = NumberFormat.getIntegerInstance()
        val formattedScore = integerInstance.format(gameScore)
        val formattedCoins = integerInstance.format(collectedCoins)
        val formattedHighScore = integerInstance.format(if (highScore > gameScore) highScore else gameScore)
        val gameOverLabel = Label("Game Over", skin)
        val yourCoinLabel = Label("Collected coins:", skin)
        val yourScoreLabel = Label("Your score:", skin)
        val highScoreLabel = Label("Highscore:", skin)
        val gameScoreLabel = Label(formattedScore, skin).apply { setAlignment(Align.center) }
        val highScoreValueLabel = Label(formattedHighScore, skin).apply { setAlignment(Align.center) }
        val coinLabel = Label(formattedCoins, skin).apply { setAlignment(Align.center) }
        val controlsParent = VerticalGroup().apply { setFillParent(true) }
        val buttonsWrapper = HorizontalGroup().apply { addActors(retryButton, exitButton) }
        controlsParent.addActors(gameOverLabel, yourScoreLabel, gameScoreLabel, highScoreLabel, highScoreValueLabel,
                yourCoinLabel, coinLabel, buttonsWrapper)
        stage.addActor(controlsParent)
    }

    override fun render(delta: Float) {
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)

    private fun persistGameData() {
        GameGlobals.addCoins(collectedCoins)
        GameGlobals.setHighScore(if (gameScore > highScore) gameScore else highScore)
        GameGlobals.persistGameData()
    }
}