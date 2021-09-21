package com.peeporunner.ecs.components.movement

enum class MovementType {
    SINE_WAVE {
        override fun calculate() {

        }
    },
    PARAMETRIC_CIRCLE {
        override fun calculate() {

        }
    };

    abstract fun calculate()
}