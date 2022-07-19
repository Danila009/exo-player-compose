package com.example.exo_player_compose.exoPlayerCompose.model

import com.example.exo_player_compose.exoPlayerCompose.type.ExoFilterType

fun exoFilterParameters(block:ExoFilterParameters.() -> Unit):ExoFilterParameters = ExoFilterParameters().apply(block)

data class ExoFilterParameters(
    var type: ExoFilterType = ExoFilterType.DEFAULT
)
