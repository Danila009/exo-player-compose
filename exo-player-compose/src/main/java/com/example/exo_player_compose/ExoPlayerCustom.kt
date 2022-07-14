package com.example.exo_player_compose

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.exo_player_compose.model.Video
import com.example.exo_player_compose.state.NextPreviousButtonPosition
import com.example.exo_player_compose.state.VideoPlayerPausePlayState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.TimeBar

private var position = -1

@Composable
fun ExoPlayerCustom(
    modifier: Modifier = Modifier,
    url: String,
    titleVideo:String = "",
    descriptionVideo:String = "",
    exoPlayer: ExoPlayer = rememberExoPlayer(),
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE,
    useController:Boolean = true,
    resizeMode:Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    progressBarUnplacedColor:Int = R.color.unplayed_color,
    progressBarBufferedColor:Int = R.color.buffered_color,
    progressBarScrubberColor:Int = Color.RED,
    progressBarPlayedColor:Int = Color.RED,
    onTitleClick:() -> Unit = {},
    onDescriptionClick:() -> Unit = {},
    onProgressBarVisibility:(Boolean) -> Unit = {}
) {

    exoPlayer(
        exoPlayer = exoPlayer,
        url = url,
        statePlayPause = statePlayPause
    )

    DisposableEffect(
        key1 = ExoPlayerAndroidView(
            modifier = modifier,
            exoPlayer = exoPlayer,
            titleVideo = titleVideo,
            descriptionVideo = descriptionVideo,
            useController = useController,
            resizeMode = resizeMode,
            progressBarUnplacedColor = progressBarUnplacedColor,
            progressBarBufferedColor = progressBarBufferedColor,
            progressBarScrubberColor = progressBarScrubberColor,
            progressBarPlayedColor = progressBarPlayedColor,
            onTitleClick = onTitleClick,
            onDescriptionClick = onDescriptionClick,
            onProgressBarVisibility = onProgressBarVisibility
        ),
        effect = {
            onDispose {
                exoPlayer.release()
            }
        }
    )
}

@Composable
fun ExoPlayerCustom(
    modifier: Modifier = Modifier,
    videoList: List<Video>,
    exoPlayer: ExoPlayer = rememberExoPlayer(),
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE,
    nextPreviousButtonPosition: NextPreviousButtonPosition = NextPreviousButtonPosition.BOTTOM,
    useController:Boolean = true,
    resizeMode:Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    progressBarUnplacedColor:Int = R.color.unplayed_color,
    progressBarBufferedColor:Int = R.color.buffered_color,
    progressBarScrubberColor:Int = Color.RED,
    progressBarPlayedColor:Int = Color.RED,
    onTitleClick:() -> Unit = {},
    onDescriptionClick:() -> Unit = {},
    onProgressBarVisibility:(Boolean) -> Unit = {}
) {

    DisposableEffect(
        key1 = ExoPlayerAndroidView(
            modifier = modifier,
            videoList = videoList,
            exoPlayer = exoPlayer,
            nextPreviousButtonPosition = nextPreviousButtonPosition,
            statePlayPause = statePlayPause,
            useController = useController,
            resizeMode = resizeMode,
            progressBarUnplacedColor = progressBarUnplacedColor,
            progressBarBufferedColor = progressBarBufferedColor,
            progressBarScrubberColor = progressBarScrubberColor,
            progressBarPlayedColor = progressBarPlayedColor,
            onTitleClick = onTitleClick,
            onDescriptionClick = onDescriptionClick,
            onProgressBarVisibility = onProgressBarVisibility
        ),
        effect = {
            onDispose {
                exoPlayer.release()
            }
        }
    )
}

@Composable
private fun ExoPlayerAndroidView(
    modifier: Modifier = Modifier,
    titleVideo:String = "",
    descriptionVideo:String = "",
    exoPlayer: ExoPlayer,
    useController:Boolean = true,
    resizeMode:Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    progressBarUnplacedColor:Int = R.color.unplayed_color,
    progressBarBufferedColor:Int = R.color.buffered_color,
    progressBarScrubberColor:Int = Color.RED,
    progressBarPlayedColor:Int = Color.RED,
    onTitleClick:() -> Unit = {},
    onDescriptionClick:() -> Unit = {},
    onProgressBarVisibility:(Boolean) -> Unit = {}
){
    AndroidView(
        modifier = modifier,
        factory = {
            View.inflate(it, R.layout.custom_styled_player, null)
        },
        update = {
            val playerView = it.findViewById<StyledPlayerView>(R.id.player_view)
            val progressBar = it.findViewById<ProgressBar>(R.id.progress_bar)
            val exoProgressBar = it.findViewById<DefaultTimeBar>(R.id.exo_progress)
            val titleView = it.findViewById<TextView>(R.id.video_title)
            val descriptionView = it.findViewById<TextView>(R.id.video_description)

            playerView(
                playerView = playerView,
                progressBar = progressBar,
                exoPlayer = exoPlayer,
                useController = useController,
                resizeMode = resizeMode,
                onProgressBarVisibility = onProgressBarVisibility
            )

            exoProgress(
                progressView = exoProgressBar,
                playerView = playerView,
                unplacedColor = progressBarUnplacedColor,
                bufferedColor = progressBarBufferedColor,
                scrubberColor = progressBarScrubberColor,
                playedColor = progressBarPlayedColor
            )

            videoTitleDescription(
                titleView = titleView,
                descriptionView = descriptionView,
                title = titleVideo,
                description = descriptionVideo,
                onTitleClick = onTitleClick,
                onDescriptionClick = onDescriptionClick
            )
        }
    )
}

@Composable
private fun ExoPlayerAndroidView(
    modifier: Modifier = Modifier,
    videoList: List<Video>,
    exoPlayer: ExoPlayer,
    nextPreviousButtonPosition: NextPreviousButtonPosition = NextPreviousButtonPosition.BOTTOM,
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE,
    useController:Boolean = true,
    resizeMode:Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    progressBarUnplacedColor:Int = R.color.unplayed_color,
    progressBarBufferedColor:Int = R.color.buffered_color,
    progressBarScrubberColor:Int = Color.RED,
    progressBarPlayedColor:Int = Color.RED,
    onTitleClick:() -> Unit = {},
    onDescriptionClick:() -> Unit = {},
    onProgressBarVisibility:(Boolean) -> Unit = {}
){
    AndroidView(
        modifier = modifier,
        factory = {
            View.inflate(it, R.layout.custom_styled_player, null)
        },
        update = {
            val playerView = it.findViewById<StyledPlayerView>(R.id.player_view)
            val progressBar = it.findViewById<ProgressBar>(R.id.progress_bar)
            val exoProgressBar = it.findViewById<DefaultTimeBar>(R.id.exo_progress)
            val titleView = it.findViewById<TextView>(R.id.video_title)
            val descriptionView = it.findViewById<TextView>(R.id.video_description)

            val bottomNextView = it.findViewById<ImageView>(R.id.bottom_skip_next)
            val bottomPreviousView = it.findViewById<ImageView>(R.id.bottom_skip_previous)

            titleView.setOnClickListener { onTitleClick() }
            descriptionView.setOnClickListener { onDescriptionClick() }

            setPosition(
                videoList = videoList,
                positionButton = nextPreviousButtonPosition,
                isIncrement = true
            )

            createPlayer(
                videoList = videoList,
                exoPlayer = exoPlayer,
                titleView = titleView,
                descriptionView = descriptionView,
                statePlayPause = statePlayPause
            )

            playerView(
                playerView = playerView,
                progressBar = progressBar,
                exoPlayer = exoPlayer,
                useController = useController,
                resizeMode = resizeMode,
                onProgressBarVisibility = onProgressBarVisibility
            )

            exoProgress(
                progressView = exoProgressBar,
                playerView = playerView,
                unplacedColor = progressBarUnplacedColor,
                bufferedColor = progressBarBufferedColor,
                scrubberColor = progressBarScrubberColor,
                playedColor = progressBarPlayedColor
            )

            nextPreviousButton(
                videoList = videoList,
                exoPlayer = exoPlayer,
                titleView = titleView,
                descriptionView = descriptionView,
                bottomNextView = bottomNextView,
                bottomPreviousView = bottomPreviousView,
                positionButton = nextPreviousButtonPosition,
                statePlayPause = statePlayPause
            )
        }
    )
}

private fun playerView(
    playerView:StyledPlayerView,
    progressBar: ProgressBar,
    exoPlayer:ExoPlayer,
    useController:Boolean,
    resizeMode:Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    onProgressBarVisibility:(Boolean) -> Unit = {}
){
    playerView.player = exoPlayer
    playerView.useController = useController

    playerView.resizeMode = resizeMode

    playerView.layoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    playerView.player?.addListener(object: Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING){
                progressBar.visibility = View.VISIBLE
                onProgressBarVisibility(true)
            } else if (playbackState == Player.STATE_READY){
                progressBar.visibility = View.GONE
                onProgressBarVisibility(false)
            }
        }
    })
}

private fun exoPlayer(
    exoPlayer: ExoPlayer,
    url:String,
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE
){
    exoPlayer.apply {
        setMediaItem(MediaItem.fromUri(url))
        prepare()

        when(statePlayPause){
            VideoPlayerPausePlayState.PAUSE -> pause()
            VideoPlayerPausePlayState.PLAY -> play()
        }
    }
}

private fun videoTitleDescription(
    titleView:TextView,
    descriptionView:TextView,
    title:String,
    description:String,
    onTitleClick:() -> Unit,
    onDescriptionClick:() -> Unit
){
    titleView.text = title
    descriptionView.text = description

    titleView.setOnClickListener { onTitleClick() }
    descriptionView.setOnClickListener { onDescriptionClick() }
}

private fun exoProgress(
    progressView:DefaultTimeBar,
    playerView:StyledPlayerView,
    unplacedColor:Int,
    bufferedColor:Int,
    scrubberColor:Int,
    playedColor:Int
){
    progressView.setUnplayedColor(unplacedColor)
    progressView.setBufferedColor(bufferedColor)
    progressView.setScrubberColor(scrubberColor)
    progressView.setPlayedColor(playedColor)

    progressView.addListener(object : TimeBar.OnScrubListener{
        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            playerView.player?.pause()
        }

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            playerView.player?.seekTo(position)
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            playerView.player?.play()
        }
    })
}

private fun nextPreviousButton(
    videoList:List<Video>,
    exoPlayer: ExoPlayer,
    titleView:TextView,
    descriptionView:TextView,
    bottomNextView:ImageView,
    bottomPreviousView:ImageView,
    positionButton: NextPreviousButtonPosition,
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE
){
    when(positionButton){
        NextPreviousButtonPosition.TOP -> {

        }
        NextPreviousButtonPosition.CENTER -> {

        }
        NextPreviousButtonPosition.BOTTOM -> {
            bottomNextView.visibility = View.VISIBLE
            bottomPreviousView.visibility = View.VISIBLE

            bottomPreviousView.setOnClickListener {
                nextButton(
                    videoList = videoList,
                    positionButton = positionButton,
                    isNext = false,
                    exoPlayer = exoPlayer,
                    titleView = titleView,
                    descriptionView = descriptionView,
                    statePlayPause = statePlayPause
                )
            }

            bottomNextView.setOnClickListener {
                nextButton(
                    videoList = videoList,
                    positionButton = positionButton,
                    isNext = true,
                    exoPlayer = exoPlayer,
                    titleView = titleView,
                    descriptionView = descriptionView,
                    statePlayPause = statePlayPause
                )
            }
        }
        NextPreviousButtonPosition.NO_ELEMENTS -> {
            bottomNextView.visibility = View.GONE
            bottomPreviousView.visibility = View.GONE
        }
    }
}

private fun nextButton(
    videoList: List<Video>,
    exoPlayer: ExoPlayer,
    titleView:TextView,
    descriptionView:TextView,
    positionButton: NextPreviousButtonPosition,
    isNext:Boolean = true,
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE
){
    if (isNext){
        setPosition(
            videoList = videoList,
            positionButton = positionButton
        )
    }else{
        if (position != 0){
            setPosition(
                videoList = videoList,
                positionButton = positionButton,
                isIncrement = false
            )
        }
    }

    createPlayer(
        videoList = videoList,
        exoPlayer = exoPlayer,
        titleView = titleView,
        descriptionView = descriptionView,
        statePlayPause = statePlayPause
    )
}

private fun setPosition(
    videoList: List<Video>,
    positionButton: NextPreviousButtonPosition,
    isIncrement:Boolean = true
){
    if(positionButton != NextPreviousButtonPosition.NO_ELEMENTS){
        if (isIncrement){
            if (videoList.size - 1 == position)
                position = 0
            else
                ++position
        }else{
            if (videoList.size - 1 == 0)
                position = videoList.size - 1
            else
                --position
        }
    }
}

private fun createPlayer(
    videoList: List<Video>,
    exoPlayer: ExoPlayer,
    titleView:TextView,
    descriptionView:TextView,
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE
){
    exoPlayer.setMediaItem(MediaItem.fromUri(videoList[position].videoUrl))
    titleView.text = videoList[position].title
    descriptionView.text = videoList[position].description
    exoPlayer.prepare()

    when(statePlayPause){
        VideoPlayerPausePlayState.PAUSE -> exoPlayer.play()
        VideoPlayerPausePlayState.PLAY -> exoPlayer.pause()
    }
}