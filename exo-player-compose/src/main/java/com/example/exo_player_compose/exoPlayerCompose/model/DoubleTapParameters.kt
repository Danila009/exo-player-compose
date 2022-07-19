package com.example.exo_player_compose.exoPlayerCompose.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes

fun doubleTapParameters(block:DoubleTapParameters.() -> Unit):DoubleTapParameters =
    DoubleTapParameters().apply(block)

/**
 * @param seekSeconds Fast forward/rewind seconds skip per tap. The text xx seconds will be changed where xx is value.
 * @param animationDuration Speed of the circle scaling / time in millis to expand completely.
 * When this time has passed, YouTubeOverlay's PerformListener.onAnimationEnd() will be called.
 * @param arcSize Arc of the background circle. The higher the value the more roundish the shape becomes.
 * This attribute should be set dynamically depending on screen size and orientation.
 * @param tapCircleColor Color of the scaling circle after tap.
 * @param backgroundCircleColor Color of the background shape.
 * @param iconAnimationDuration Time in millis to run through an full fade cycle.
 * @param icon One of the three forward icons. Will be multiplied by three and mirrored for rewind.
 * @param textAppearance Text appearance for the xx seconds text.
 * */
data class DoubleTapParameters(
    var enabled:Boolean = false,
    var seekSeconds:Int = 10,
    var animationDuration:Long = 650,
    var arcSize:Float = 80f,
    @ColorRes var tapCircleColor: Int? = null,
    @ColorRes var backgroundCircleColor:Int? = null,
    var iconAnimationDuration:Long = 750,
    @DrawableRes var icon: Int ? = null,
    @StyleRes var textAppearance:Int ? = null
)