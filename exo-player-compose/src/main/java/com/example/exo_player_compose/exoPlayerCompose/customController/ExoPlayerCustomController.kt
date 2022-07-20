package com.example.exo_player_compose.exoPlayerCompose.customController

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.example.exo_player_compose.R

@Composable
fun styledPlayerView(
    block: StyledPlayerView.() -> Unit
): StyledPlayerView {
    val context = LocalContext.current
    return StyledPlayerView(context).apply(block)
}

@Composable
fun exoPlayer(
    block: ExoPlayer.() -> Unit
):ExoPlayer {
    val context = LocalContext.current
    return ExoPlayer.Builder(context)
        .build()
        .apply(block)
}

@Composable
fun rememberExoPlayer():ExoPlayer {
    val context = LocalContext.current
    return remember {
        ExoPlayer.Builder(context)
            .build()
    }
}

@Composable
fun rememberStyledPlayerView(
    exoPlayer: ExoPlayer
) : StyledPlayerView {
    val context = LocalContext.current

    return StyledPlayerView(context).apply {
        player = exoPlayer
        useController = true
    }
}

@Composable
fun ExoPlayerCustomController(
    modifier: Modifier = Modifier,
    styledPlayerView: StyledPlayerView.() -> Unit = { },
    controller: @Composable BoxScope.() -> Unit
) {
    var isPlayerUiVisible by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val view = View.inflate(context,R.layout.exo_player, null)

                view.apply {
                    val playerView = findViewById<StyledPlayerView>(R.id.base_player_view).apply(styledPlayerView)

                    playerView.setControllerVisibilityListener(StyledPlayerView.ControllerVisibilityListener {
                        isPlayerUiVisible = if (isPlayerUiVisible) {
                            it == View.VISIBLE
                        } else {
                            true
                        }
                    })
                }
            }
        )

        if (isPlayerUiVisible){
            controller()
        }
    }
}