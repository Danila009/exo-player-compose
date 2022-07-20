package com.example.exo_player_compose.exoPlayerCompose.customController

import android.annotation.SuppressLint
import android.util.Log
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
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar

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
    exoPlayer: ExoPlayer? = null
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
    styledPlayerView: StyledPlayerView.() -> Unit = {},
    onPlayerUiVisible:(Boolean) -> Unit = {},
    controller: @Composable BoxScope.() -> Unit
) {
    var isPlayerUiVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = isPlayerUiVisible, block = {
        onPlayerUiVisible(isPlayerUiVisible)
    })

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

                    playerView.player?.let { player ->
                        val desiredPosition = player.duration / 2.toLong()
                        player.seekTo(desiredPosition)
                    }

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

@SuppressLint("ResourceAsColor")
@Composable
fun DefaultTimeBar(
    modifier: Modifier = Modifier,
    player: Player? = rememberExoPlayer(),
    block: DefaultTimeBar.() -> Unit = {}
) {
    val position by rememberSaveable{ mutableStateOf(player?.currentPosition ?: 0L) }
    val bufferedPosition by rememberSaveable{ mutableStateOf(player?.contentBufferedPosition ?: 0L) }

    LaunchedEffect(key1 = position, block = {
        Log.e("Position", position.toString())
    })

    LaunchedEffect(key1 = bufferedPosition, block = {
        Log.e("Position", bufferedPosition.toString())
    })

    AndroidView(
        modifier = modifier,
        factory = {
            DefaultTimeBar(it).apply(block).apply {
                setPosition(100000L)
                setBufferedPosition(10000000L)

                setBackgroundColor(android.R.color.holo_red_dark)

                setUnplayedColor(R.color.purple_700)
                setBufferedColor(R.color.colorAccent)
                setPlayedColor(R.color.teal_700)
                setScrubberColor(android.R.color.holo_red_dark)
            }
        }
    )
}