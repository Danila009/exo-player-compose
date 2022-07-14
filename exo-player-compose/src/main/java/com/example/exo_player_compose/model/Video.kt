package com.example.exo_player_compose.model

data class Video(
    val videoUrl:String,
    val title:String = "",
    val description:String = "",
    val previewUrl:String? = null
)
