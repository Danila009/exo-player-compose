package com.example.exoplayer_compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exo_player_compose.ExoPlayerCustom
import com.example.exo_player_compose.model.VideoExoPlayer
import com.example.exoplayer_compose.ui.theme.ExoPlayerComposeTheme
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout

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
                var resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                ExoPlayerCustom(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    videoList = videoList,
                    resizeMode = resizeMode,
                    onProgressBarVisibility = {
                        Log.d("onProgressBarVisibility",it.toString())
                    },
                    onVideoListPositionItem = {
                        Log.d("onVideoListPositionItem",it.toString())
                    },
                    onFullscreen = {
                        Log.d("onFullscreen",it.toString())
                    }
                ){
                    LazyRow(content = {
                        items(videoList){ item ->
                            Text(
                                modifier = Modifier.clickable { 
                                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                },
                                text = item.title
                            )
                        }
                    })
                }
            }
        }
    }
}