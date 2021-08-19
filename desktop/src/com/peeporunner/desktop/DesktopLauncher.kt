package com.peeporunner.desktop

import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.peeporunner.PeepoRunnerGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = 1024
        config.height = 720
        config.title = "Peepo Run - run for your life!"
        config.foregroundFPS = 60
        LwjglApplication(PeepoRunnerGame(), config)
    }
}