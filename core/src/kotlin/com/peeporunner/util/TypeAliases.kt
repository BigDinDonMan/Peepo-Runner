package com.peeporunner.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.peeporunner.ecs.components.AnimationComponent

typealias CollisionHandler = (Entity) -> Unit
typealias Action = () -> Unit
typealias AnimationSet = HashMap<String, AnimationComponent.AnimationStateData>
typealias AudioSet = HashMap<String, Sound>
typealias MusicSet = HashMap<String, Music>