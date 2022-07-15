package com.example.exo_player_compose

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.exo_player_compose.dialogs.AudioTrackDialog
import com.example.exo_player_compose.dialogs.SpeedDialog
import com.example.exo_player_compose.model.VideoExoPlayer
import com.example.exo_player_compose.state.NextPreviousButtonPosition
import com.example.exo_player_compose.state.VideoPlayerPausePlayState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.TimeBar
import java.util.*
import kotlin.collections.ArrayList

private var position = -1
private var videoFullscreen = false
private var subtitle = false
private var speed  = 1.0f

@SuppressLint("StaticFieldLeak")
private var trackSelector:DefaultTrackSelector? = null

private val audioTrack = ArrayList<String>()

@Composable
private fun rememberExoPlayer():ExoPlayer {
    val context = LocalContext.current
    trackSelector = DefaultTrackSelector(context)
    return remember {
        ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector!!)
            .build()
    }
}

@Composable
fun ExoPlayerCustom(
    modifier: Modifier = Modifier,
    url: String,
    titleVideo:String = "",
    descriptionVideo:String = "",
    fullscreen:Boolean = false,
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
    onProgressBarVisibility:(Boolean) -> Unit = {},
    onFullscreen:(Boolean) -> Unit  = {},
    onMenuClick:(() -> Unit)? = null
) {

    var audioTrackDialog by remember { mutableStateOf(false) }
    var speedDialog by remember { mutableStateOf(false) }

    videoFullscreen = fullscreen
    onFullscreen(videoFullscreen)

    if (audioTrackDialog){
        AudioTrackDialog(
            audioTrack = audioTrack,
            onDismissRequest = {
                audioTrackDialog = false
            },
        ){ audioTrackItem ->
            trackSelector?.let {
                trackSelector!!.setParameters(
                    trackSelector!!.buildUponParameters().setPreferredAudioLanguage(audioTrack[audioTrackItem.position])
                )
            }
        }
    }

    if (speedDialog){
        SpeedDialog(
            onDismissRequest = { speedDialog = false },
            onClick = {
                speed = it
                changeSpeed(
                    player = exoPlayer
                )
            }
        )
    }

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
            onProgressBarVisibility = onProgressBarVisibility,
            onMenuClick = onMenuClick,
            onFullscreen = {
                videoFullscreen = it
                onFullscreen(it)
            },
            onAudioTrackClick = {
                audioTrackDialog = true
            },
            onSpeedClick = {
                speedDialog = true
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
fun ExoPlayerCustom(
    modifier: Modifier = Modifier,
    videoList: List<VideoExoPlayer>,
    fullscreen: Boolean = false,
    repeat:Boolean = true,
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
    onProgressBarVisibility:(Boolean) -> Unit = {},
    onFullscreen:(Boolean) -> Unit  = {},
    onMenuClick:(() -> Unit)? = null,
    onVideoListPositionItem: (VideoExoPlayer) -> Unit = {}
) {
    var audioTrackDialog by remember { mutableStateOf(false) }
    var speedDialog by remember { mutableStateOf(false) }

    videoFullscreen = fullscreen
    onFullscreen(videoFullscreen)

    LaunchedEffect(key1 = position, block = {
        if (position >= 0){
            onVideoListPositionItem(videoList[position])
        }
    })

    if (audioTrackDialog){
        AudioTrackDialog(
            audioTrack = audioTrack,
            onDismissRequest = {
                audioTrackDialog = false
            },
        ){ audioTrackItem ->
            trackSelector?.let {
                trackSelector!!.setParameters(
                    trackSelector!!.buildUponParameters().setPreferredAudioLanguage(audioTrack[audioTrackItem.position])
                )
            }
        }
    }

    if (speedDialog){
        SpeedDialog(
            onDismissRequest = { speedDialog = false },
            onClick = {
                speed = it
                changeSpeed(
                    player = exoPlayer
                )
            }
        )
    }

    DisposableEffect(
        key1 = ExoPlayerAndroidView(
            modifier = modifier,
            videoList = videoList,
            repeat = repeat,
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
            onProgressBarVisibility = onProgressBarVisibility,
            onMenuClick = onMenuClick,
            onFullscreen = {
                videoFullscreen = it
                onFullscreen(it)
            },
            onAudioTrackClick = {
                audioTrackDialog = true
            },
            onSpeedClick = {
                speedDialog = true
            }
        ),
        effect = {
            onDispose {
                exoPlayer.release()
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
fun ExoPlayerCustom(
    modifier: Modifier = Modifier,
    videoList: List<VideoExoPlayer>,
    fullscreen:Boolean = false,
    drawerState:BottomDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed),
    repeat:Boolean = true,
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
    onProgressBarVisibility:(Boolean) -> Unit = {},
    onVideoListPositionItem: (VideoExoPlayer) -> Unit = {},
    onFullscreen:(Boolean) -> Unit  = {},
    onMenuClick:(() -> Unit)? = null,
    drawerContent: @Composable ColumnScope.() -> Unit
) {
    var audioTrackDialog by remember { mutableStateOf(false) }
    var speedDialog by remember { mutableStateOf(false) }

    videoFullscreen = fullscreen
    onFullscreen(videoFullscreen)

    LaunchedEffect(key1 = position, block = {
        if (position >= 0){
            onVideoListPositionItem(videoList[position])
        }
    })

    if (audioTrackDialog){
        AudioTrackDialog(
            audioTrack = audioTrack,
            onDismissRequest = {
                audioTrackDialog = false
            },
        ){ audioTrackItem ->
            trackSelector?.let {
                trackSelector!!.setParameters(
                    trackSelector!!.buildUponParameters().setPreferredAudioLanguage(audioTrack[audioTrackItem.position])
                )
            }
        }
    }

    if (speedDialog){
        SpeedDialog(
            onDismissRequest = { speedDialog = false },
            onClick = {
                speed = it
                changeSpeed(
                    player = exoPlayer
                )
            }
        )
    }

    BottomDrawerExoPlayer(
        drawerState = drawerState,
        drawerContent = drawerContent
    ){
        DisposableEffect(
            key1 = ExoPlayerAndroidView(
                modifier = modifier,
                videoList = videoList,
                repeat = repeat,
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
                onProgressBarVisibility = onProgressBarVisibility,
                onMenuClick = onMenuClick,
                onFullscreen = {
                    videoFullscreen = it
                    onFullscreen(it)
                },
                onAudioTrackClick = {
                    audioTrackDialog = true
                },
                onSpeedClick = {
                    speedDialog = true
                }
            ),
            effect = {
                onDispose {
                    exoPlayer.release()
                }
            }
        )
    }
}

@Composable
private fun ExoPlayerAndroidView(
    modifier: Modifier = Modifier,
    videoList: List<VideoExoPlayer>,
    repeat: Boolean,
    exoPlayer: ExoPlayer,
    nextPreviousButtonPosition: NextPreviousButtonPosition = NextPreviousButtonPosition.BOTTOM,
    statePlayPause: VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE,
    useController:Boolean = true,
    resizeMode:Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    progressBarUnplacedColor:Int = R.color.unplayed_color,
    progressBarBufferedColor:Int = R.color.buffered_color,
    progressBarScrubberColor:Int = Color.RED,
    progressBarPlayedColor:Int = Color.RED,
    onTitleClick:() -> Unit = {},
    onDescriptionClick:() -> Unit = {},
    onProgressBarVisibility:(Boolean) -> Unit = {},
    onFullscreen:(Boolean) -> Unit,
    onMenuClick:(() -> Unit)?,
    onAudioTrackClick:() -> Unit,
    onSpeedClick:() -> Unit
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

            val fullscreenView = it.findViewById<ImageView>(R.id.fullscreen)
            val audioTrackView = it.findViewById<ImageView>(R.id.audio_track)
            val menuView = it.findViewById<ImageView>(R.id.menu)
            val subtitleView = it.findViewById<ImageView>(R.id.subtitles)
            val speedView = it.findViewById<ImageView>(R.id.speed)

            titleView.setOnClickListener { onTitleClick() }
            descriptionView.setOnClickListener { onDescriptionClick() }

            audioTrackView.setOnClickListener { onAudioTrackClick() }
            speedView.setOnClickListener { onSpeedClick() }

            if (repeat){
                repeat(
                    exoPlayer = exoPlayer,
                    videoList = videoList,
                    titleView = titleView,
                    descriptionView = descriptionView,
                    positionButton = nextPreviousButtonPosition,
                    statePlayPause = statePlayPause
                )
            }

            if (videoFullscreen){
                fullscreenView.setImageResource(R.drawable.ic_fullscreen)
            }else{
                fullscreenView.setImageResource(R.drawable.ic_fullscreen_exit)
            }

            fullscreenView.setOnClickListener {
                videoFullscreen = if (videoFullscreen){
                    fullscreenView.setImageResource(R.drawable.ic_fullscreen_exit)
                    false
                }else {
                    fullscreenView.setImageResource(R.drawable.ic_fullscreen)
                    true
                }
                onFullscreen(videoFullscreen)
            }

            if(onMenuClick != null){
                menuView.setOnClickListener { onMenuClick() }
                menuView.visibility = View.VISIBLE
            }else {
                menuView.visibility = View.GONE
            }

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

            audioTrack(
                player = exoPlayer,
                audioTrackView = audioTrackView
            )

            subtitle(
                context = it.context,
                subtitleView = subtitleView
            )
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
    onProgressBarVisibility:(Boolean) -> Unit = {},
    onFullscreen: (Boolean) -> Unit,
    onMenuClick:(() -> Unit)?,
    onAudioTrackClick:() -> Unit,
    onSpeedClick:() -> Unit
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

            val fullscreenView = it.findViewById<ImageView>(R.id.fullscreen)

            val menuView = it.findViewById<ImageView>(R.id.menu)
            val audioTrackView = it.findViewById<ImageView>(R.id.audio_track)
            val subtitleView = it.findViewById<ImageView>(R.id.subtitles)
            val speedView = it.findViewById<ImageView>(R.id.speed)

            audioTrackView.setOnClickListener { onAudioTrackClick() }
            speedView.setOnClickListener { onSpeedClick() }

            if (videoFullscreen){
                fullscreenView.setImageResource(R.drawable.ic_fullscreen)
            }else{
                fullscreenView.setImageResource(R.drawable.ic_fullscreen_exit)
            }

            fullscreenView.setOnClickListener {
                videoFullscreen = if (videoFullscreen){
                    fullscreenView.setImageResource(R.drawable.ic_fullscreen_exit)
                    false
                }else {
                    fullscreenView.setImageResource(R.drawable.ic_fullscreen)
                    true
                }
                onFullscreen(videoFullscreen)
            }

            if(onMenuClick != null){
                menuView.setOnClickListener { onMenuClick() }
                menuView.visibility = View.VISIBLE
            }else {
                menuView.visibility = View.GONE
            }

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

            audioTrack(
                player = exoPlayer,
                audioTrackView = audioTrackView
            )

            subtitle(
                context = it.context,
                subtitleView = subtitleView
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
    videoList:List<VideoExoPlayer>,
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
    videoList: List<VideoExoPlayer>,
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
    videoList: List<VideoExoPlayer>,
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
    videoList: List<VideoExoPlayer>,
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

private fun repeat(
    exoPlayer: ExoPlayer,
    videoList: List<VideoExoPlayer>,
    titleView: TextView,
    descriptionView: TextView,
    positionButton: NextPreviousButtonPosition,
    statePlayPause:VideoPlayerPausePlayState = VideoPlayerPausePlayState.PAUSE
){
    exoPlayer.repeatMode = Player.REPEAT_MODE_OFF

    exoPlayer.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED){
                nextButton(
                    exoPlayer = exoPlayer,
                    videoList = videoList,
                    titleView = titleView,
                    descriptionView = descriptionView,
                    positionButton = positionButton,
                    statePlayPause = statePlayPause,
                    isNext = true
                )
            }
        }
    })
}

private fun audioTrack(
    player: ExoPlayer,
    audioTrackView:ImageView
){
    if (audioTrack.isNotEmpty()){
        audioTrackView.visibility = View.VISIBLE
    }else{
        audioTrackView.visibility = View.GONE
    }

    for (i in 0 until player.currentTrackGroups.length){
        if (
            player.currentTrackGroups.get(i).getFormat(0).selectionFlags == C.SELECTION_FLAG_DEFAULT
        ){
            audioTrack.add(
                Locale(player.currentTrackGroups.get(i).getFormat(1).language.toString()).displayLanguage
            )
        }
    }
}

private fun subtitle(
    context: Context,
    subtitleView:ImageView
){
    subtitleView.setOnClickListener {
        if (subtitle){
            subtitleView.setImageResource(R.drawable.ic_subtitles_off)
            trackSelector?.parameters = DefaultTrackSelector.ParametersBuilder(context)
                .setRendererDisabled(
                    C.TRACK_TYPE_VIDEO, true
                )
                .build()
            subtitle = false
        }else{
            subtitleView.setImageResource(R.drawable.ic_subtitles)
            trackSelector?.parameters = DefaultTrackSelector.ParametersBuilder(context)
                .setRendererDisabled(
                    C.TRACK_TYPE_VIDEO, false
                )
                .build()
            subtitle = true
        }
    }
}

private fun changeSpeed(
    player: ExoPlayer
){
    player.setPlaybackSpeed(speed)
}