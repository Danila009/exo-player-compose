package com.example.exo_player_compose.exoPlayerCompose.model

import com.example.exo_player_compose.exoPlayerCompose.state.VideoPlayerPausePlayState

fun exoParameters(block:ExoParameters.() -> Unit):ExoParameters = ExoParameters().apply(block)

data class ExoParameters(
    var url: String = "",
    var useController:Boolean = true,
    var statePlayPause: VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE
)