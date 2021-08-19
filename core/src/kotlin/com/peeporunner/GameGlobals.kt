package com.peeporunner

import com.badlogic.gdx.Gdx
import kotlin.properties.Delegates


object GameGlobals {
    private val gameDataPrefs = Gdx.app.getPreferences("default")
    private var coins by Delegates.observable(gameDataPrefs.getInteger("coins", 0)) {_, _, newValue -> kotlin.run {
        gameDataPrefs.putInteger("coins", newValue)
    }}

    fun addCoins(value: Int) {
        coins += value
    }

    fun subtractCoins(value: Int) {
        coins -= value
    }

    fun setHighScore(score: Int) {
        gameDataPrefs.putInteger("highscore", score)
    }

    fun highScore(): Int = gameDataPrefs.getInteger("highscore", 0)

    fun coins(): Int = coins

    fun persistGameData() = gameDataPrefs.flush()
}
