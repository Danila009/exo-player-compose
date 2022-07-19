package com.example.exo_player_compose.exoPlayerCompose.model

fun videoExoPlayer(block:VideoExoPlayer.() -> Unit):VideoExoPlayer = VideoExoPlayer().apply(block)

data class VideoExoPlayer(
    val videoUrl:String = "",
    val title:String = "",
    val description:String = ""
)
