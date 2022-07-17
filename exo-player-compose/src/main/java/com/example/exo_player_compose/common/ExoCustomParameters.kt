package com.example.exo_player_compose.common

import android.graphics.Color
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import com.example.exo_player_compose.R
import com.example.exo_player_compose.model.VideoExoPlayer
import com.example.exo_player_compose.state.NextPreviousButtonPosition
import com.example.exo_player_compose.state.VideoPlayerPausePlayState
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout

@ExperimentalMaterialApi
fun exoCustomParameters(block:ExoCustomParameters.() -> Unit):ExoCustomParameters = ExoCustomParameters().apply(block)

@ExperimentalMaterialApi
data class ExoCustomParameters(
    var useController:Boolean = true,
    var title:String = "",
    var description:String = "",
    var fullscreen:Boolean = false,
    var repeatMode:Boolean = false,
    var subtitle:Boolean = false,
    var position:Int = -1,
    var speed:Float = 1f,
    var trackSelector:DefaultTrackSelector? = null,
    var audioTrack:List<String> = emptyList(),
    var statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE,
    var nextPreviousButtonPosition: NextPreviousButtonPosition = NextPreviousButtonPosition.NO_ELEMENTS,
    var resizeMode:Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    var progressBarUnplacedColor:Int = R.color.unplayed_color,
    var progressBarBufferedColor:Int = R.color.buffered_color,
    var progressBarScrubberColor:Int = Color.RED,
    var progressBarPlayedColor:Int = Color.RED,
    var onTitleClick:() -> Unit = {},
    var onDescriptionClick:() -> Unit = {},
    var onProgressBarVisibility:(Boolean) -> Unit = {},
    var onFullscreen:(Boolean) -> Unit  = {},
    var onMenuClick:(() -> Unit)? = null,
    var onVideoListPositionItem: (VideoExoPlayer) -> Unit = {},
    var onSpeed:(Float) -> Unit = {},
    var onSubtitle:(Boolean) -> Unit = {},
    var drawerState: BottomDrawerState? = null,
    var drawerContent: (@Composable ColumnScope.() -> Unit)? = null
)