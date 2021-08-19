package com.peeporunner

import com.badlogic.gdx.Gdx

object GamePreferences {
    private val options = Gdx.app.getPreferences("options")

    fun save() = options.flush()
}