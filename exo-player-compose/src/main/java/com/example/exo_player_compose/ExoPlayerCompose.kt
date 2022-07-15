package com.example.exo_player_compose

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.exo_player_compose.state.VideoPlayerPausePlayState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun ExoPlayer(
    modifier: Modifier = Modifier,
    url: String,
    exoPlayer: ExoPlayer = rememberExoPlayer(),
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE,
    useController:Boolean = true
) {
    val context = LocalContext.current

    exoPlayer.apply {
        setMediaItem(MediaItem.fromUri(url))
        prepare()

        when(statePlayPause){
            VideoPlayerPausePlayState.PAUSE -> pause()
            VideoPlayerPausePlayState.PLAY -> play()
        }
    }

    DisposableEffect(
        key1 = AndroidView(
            modifier = modifier,
            factory = {
                StyledPlayerView(context).apply {
                    player = exoPlayer
                    this.useController = useController

                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    (player as ExoPlayer).addListener(object : Player.Listener{
                        override fun onPlaybackStateChanged(playbackState: Int) {

                        }
                    })
                }
            },
            update = {

            }
        ),
        effect = {
            onDispose {
                exoPlayer.release()
            }
        }
    )
}

@Composable
private fun rememberExoPlayer():ExoPlayer {
    val context = LocalContext.current
    return remember {
        ExoPlayer.Builder(context)
            .build()
    }
}