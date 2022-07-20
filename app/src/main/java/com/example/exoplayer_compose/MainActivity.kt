package com.example.exoplayer_compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.exo_player_compose.exoPlayerCompose.customController.DefaultTimeBar
import com.example.exo_player_compose.exoPlayerCompose.customController.ExoPlayerCustomController
import com.example.exo_player_compose.exoPlayerCompose.customController.exoPlayer
import com.example.exo_player_compose.exoPlayerCompose.model.*
import com.example.exo_player_compose.exoPlayerCompose.state.VideoPlayerPausePlayState
import com.example.exo_player_compose.exoPlayerCompose.type.ExoFilterType
import com.example.exoplayer_compose.ui.theme.ExoPlayerComposeTheme
import com.google.android.exoplayer2.MediaItem

private const val VIDEO_URL = "https://firebasestorage.googleapis.com/v0/b/fir-939ca.appspot.com/o/videos%2FSnatch.2000.iPad.1024x.leonardo59.BDRip.mp4?alt=media&token=bd9aebf3-c92f-4c6c-96cd-6ad4d8fb2a33"
private const val VIDEO_2_URL = "https://api.cfif31.ru/storeApp/api/Product/2/Video.mp4"
private const val VIDEO_3_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
private const val VIDEO_4_URL = "https://storage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"

private val videoList = listOf(
    VideoExoPlayer(
        videoUrl = VIDEO_2_URL,
        title = "test_1",
        description = "test_1"
    ),
    VideoExoPlayer(
        videoUrl = VIDEO_3_URL,
        title = "test_2",
        description = "test_2"
    ),
    VideoExoPlayer(
        videoUrl = VIDEO_4_URL,
        title = "test_3",
        description = "test_3"
    )
)


@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExoPlayerComposeTheme {

                val useController by rememberSaveable { mutableStateOf(true) }
                val statePlayPause by rememberSaveable { mutableStateOf(VideoPlayerPausePlayState.PLAY) }

                val exoParameters = exoCustomParameters {

                    this.useController = useController

                    this.statePlayPause = statePlayPause

                    doubleTapParameters = doubleTapParameters {
                        enabled = true
                    }

                    previewSeekBarParameters = previewSeekBarParameters {
                        enabled = true
                        previewUrl = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/thumbnails/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.jpg"
                    }

                    exoFilterParameters = exoFilterParameters {
                        type = ExoFilterType.INVERT
                    }

                    onProgressBarVisibility = {
                        Log.d("onProgressBarVisibility",it.toString())
                    }

                    onVideoListPositionItem = {
                        Log.d("onVideoListPositionItem",it.toString())
                    }

                    onFullscreen = {
                        Log.d("onFullscreen",it.toString())
                    }
                }


                val context = LocalContext.current

                val exoPlayer = exoPlayer {
                    setMediaItem(MediaItem.fromUri(VIDEO_2_URL))
                    prepare()
                    play()
                }

                ExoPlayerCustomController(
                    styledPlayerView = {
                        player = exoPlayer

                        this.useController = true
                    }
                ) {
                    Column {
                        Text(text = "Test")

                        DefaultTimeBar(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            player = exoPlayer
                        ){

                        }
                    }
                }

//                ExoPlayerCustom(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp),
//                    url = VIDEO_2_URL,
//                    parameters = exoParameters
//                )
            }
        }
    }
}