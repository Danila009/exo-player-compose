package com.example.exo_player_compose.exoPlayerCompose.model

import com.example.exo_player_compose.previewSeekBar.PreviewSeekBarAnimationType

fun previewSeekBarParameters(block:PreviewSeekBarParameters.() -> Unit):PreviewSeekBarParameters =
    PreviewSeekBarParameters().apply(block)

/**
 * @param previewUrl [example url](https://bitdash-a.akamaihd.net/content/MI201109210084_1/thumbnails/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.jpg)
 * */
data class PreviewSeekBarParameters(
    var enabled:Boolean = false,
    var previewUrl:String = "",
    var previewAutoHideSwitch:Boolean = true,
    var previewAnimationEnabled:Boolean = true,
    var typeAnimation:PreviewSeekBarAnimationType = PreviewSeekBarAnimationType.MORPH_ANIMATION
)