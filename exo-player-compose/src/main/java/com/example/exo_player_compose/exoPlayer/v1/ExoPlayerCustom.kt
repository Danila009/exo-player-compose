package com.example.exo_player_compose.exoPlayer.v1

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.exo_player_compose.R
import com.example.exo_player_compose.common.ExoCustomParameters
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

@ExperimentalMaterialApi
@Composable
fun ExoPlayerCustom(
    modifier: Modifier = Modifier,
    url: String,
    parameters: ExoCustomParameters
) {

    val exoPlayer = rememberExoPlayer()

    var audioTrackDialog by rememberSaveable { mutableStateOf(false) }
    var speedDialog by rememberSaveable { mutableStateOf(false) }

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

    LaunchedEffect(key1 = Unit, block = {
        audioTrack(player = exoPlayer)

        videoFullscreen = parameters.fullscreen
        speed = parameters.speed
        subtitle = parameters.subtitle
        position = parameters.position

        parameters.audioTrack = audioTrack
    })

    LaunchedEffect(key1 = subtitle, block = {
        parameters.onSubtitle(subtitle)
    })

    LaunchedEffect(key1 = speed, block = {
        parameters.onSpeed(speed)
    })

    LaunchedEffect(key1 = videoFullscreen, block = {
        parameters.onFullscreen(videoFullscreen)
    })

    LaunchedEffect(key1 = trackSelector, block = {
        parameters.trackSelector = trackSelector
    })

    exoPlayer(
        exoPlayer = exoPlayer,
        parameters = parameters,
        url = url
    )

    BottomDrawerExoPlayer(
        drawerState = parameters.drawerState ?: rememberBottomDrawerState(BottomDrawerValue.Closed),
        drawerContent = parameters.drawerContent
    ){
        DisposableEffect(
            key1 = ExoPlayerAndroidView(
                modifier = modifier,
                exoPlayer = exoPlayer,
                parameters = parameters,
                onFullscreen = {
                    videoFullscreen = it
                    parameters.onFullscreen(it)
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

@ExperimentalMaterialApi
@Composable
fun ExoPlayerCustom(
    modifier: Modifier = Modifier,
    parameters: ExoCustomParameters,
    videoList: List<VideoExoPlayer>,
) {
    val exoPlayer = rememberExoPlayer()

    var audioTrackDialog by rememberSaveable { mutableStateOf(false) }
    var speedDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit, block = {
        videoFullscreen = parameters.fullscreen
        speed = parameters.speed
        subtitle = parameters.subtitle
        position = parameters.position

        if (position == -1){
            setPosition(
                videoList = videoList,
                positionButton = parameters.nextPreviousButtonPosition,
                isIncrement = true
            )
        }
    })

    LaunchedEffect(key1 = position, block = {
        if (position >= 0){
            parameters.onVideoListPositionItem(videoList[position])
        }
    })

    LaunchedEffect(key1 = trackSelector, block = {
        parameters.trackSelector = trackSelector
    })

    LaunchedEffect(key1 = speed, block = {
        parameters.onSpeed(speed)
    })

    LaunchedEffect(key1 = videoFullscreen, block = {
        parameters.onFullscreen(videoFullscreen)
    })

    LaunchedEffect(key1 = subtitle, block = {
        parameters.onSubtitle(subtitle)
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
        drawerState = parameters.drawerState ?: rememberBottomDrawerState(BottomDrawerValue.Closed),
        drawerContent = parameters.drawerContent
    ){
        DisposableEffect(
            key1 = ExoPlayerAndroidView(
                modifier = modifier,
                videoList = videoList,
                parameters = parameters,
                exoPlayer = exoPlayer,
                onFullscreen = {
                    videoFullscreen = it
                    parameters.onFullscreen(it)
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

//@ExperimentalMaterialApi
//@Composable
//fun ExoPlayerCustom(
//    modifier: Modifier = Modifier,
//    parameters:ExoCustomParameters,
//    videoList: List<VideoExoPlayer>,
//    drawerState: BottomDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
//) {
//    val exoPlayer = rememberExoPlayer()
//
//    var audioTrackDialog by rememberSaveable { mutableStateOf(false) }
//    var speedDialog by rememberSaveable { mutableStateOf(false) }
//
//    LaunchedEffect(key1 = Unit, block = {
//        videoFullscreen = parameters.fullscreen
//        speed = parameters.speed
//        subtitle = parameters.subtitle
//        position = parameters.position
//
//        if (position == -1){
//            setPosition(
//                videoList = videoList,
//                positionButton = parameters.nextPreviousButtonPosition,
//                isIncrement = true
//            )
//        }
//    })
//
//    LaunchedEffect(key1 = subtitle, block = {
//        parameters.onSubtitle(subtitle)
//    })
//
//    LaunchedEffect(key1 = videoFullscreen, block = {
//        parameters.onFullscreen(videoFullscreen)
//    })
//
//    LaunchedEffect(key1 = trackSelector, block = {
//        parameters.trackSelector = trackSelector
//    })
//
//    LaunchedEffect(key1 = position, block = {
//        if (position >= 0){
//            parameters.onVideoListPositionItem(videoList[position])
//        }
//    })
//
//    LaunchedEffect(key1 = speed, block = {
//        parameters.onSpeed(speed)
//    })
//
//    if (audioTrackDialog){
//        AudioTrackDialog(
//            audioTrack = audioTrack,
//            onDismissRequest = {
//                audioTrackDialog = false
//            },
//        ){ audioTrackItem ->
//            trackSelector?.let {
//                trackSelector!!.setParameters(
//                    trackSelector!!.buildUponParameters().setPreferredAudioLanguage(audioTrack[audioTrackItem.position])
//                )
//            }
//        }
//    }
//
//    if (speedDialog){
//        SpeedDialog(
//            onDismissRequest = { speedDialog = false },
//            onClick = {
//                speed = it
//                changeSpeed(
//                    player = exoPlayer
//                )
//            }
//        )
//    }
//
//    BottomDrawerExoPlayer(
//        drawerState = drawerState,
//        drawerContent = drawerContent
//    ){
//        DisposableEffect(
//            key1 = ExoPlayerAndroidView(
//                modifier = modifier,
//                videoList = videoList,
//                exoPlayer = exoPlayer,
//                parameters = parameters,
//                onFullscreen = {
//                    videoFullscreen = it
//                    parameters.onFullscreen(it)
//                },
//                onAudioTrackClick = {
//                    audioTrackDialog = true
//                },
//                onSpeedClick = {
//                    speedDialog = true
//                }
//            ),
//            effect = {
//                onDispose {
//                    exoPlayer.release()
//                }
//            }
//        )
//    }
//}

@ExperimentalMaterialApi
@Composable
private fun ExoPlayerAndroidView(
    modifier: Modifier = Modifier,
    videoList: List<VideoExoPlayer>,
    parameters: ExoCustomParameters,
    exoPlayer: ExoPlayer,
    onFullscreen:(Boolean) -> Unit,
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
            val pipModeView = it.findViewById<ImageView>(R.id.pip_mode)

            titleView.setOnClickListener { parameters.onTitleClick() }
            descriptionView.setOnClickListener { parameters.onDescriptionClick() }

            audioTrackView.setOnClickListener { onAudioTrackClick() }
            speedView.setOnClickListener { onSpeedClick() }

            if (parameters.repeatMode){
                repeat(
                    exoPlayer = exoPlayer,
                    videoList = videoList,
                    titleView = titleView,
                    descriptionView = descriptionView,
                    parameters = parameters
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

            if(parameters.onMenuClick != null){
                menuView.setOnClickListener { parameters.onMenuClick?.let { it1 -> it1() } }
                menuView.visibility = View.VISIBLE
            }else {
                menuView.visibility = View.GONE
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pipModeView.visibility = View.VISIBLE
            }else {
                pipModeView.visibility = View.GONE
            }

            pipModeView.setOnClickListener { view ->
                pipMode(
                    context = view.context,
                    playerView = playerView
                )
            }

            createPlayer(
                videoList = videoList,
                exoPlayer = exoPlayer,
                titleView = titleView,
                descriptionView = descriptionView,
                parameters = parameters
            )

            playerView(
                playerView = playerView,
                progressBar = progressBar,
                exoPlayer = exoPlayer,
                parameters = parameters
            )

            exoProgress(
                progressView = exoProgressBar,
                playerView = playerView,
                parameters = parameters
            )

            nextPreviousButton(
                videoList = videoList,
                exoPlayer = exoPlayer,
                titleView = titleView,
                descriptionView = descriptionView,
                bottomNextView = bottomNextView,
                bottomPreviousView = bottomPreviousView,
                parameters = parameters
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

@ExperimentalMaterialApi
@Composable
private fun ExoPlayerAndroidView(
    modifier: Modifier = Modifier,
    parameters: ExoCustomParameters,
    exoPlayer: ExoPlayer,
    onFullscreen: (Boolean) -> Unit,
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
            val pipModeView = it.findViewById<ImageView>(R.id.pip_mode)

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

            if(parameters.onMenuClick != null){
                menuView.setOnClickListener { parameters.onMenuClick?.let { it1 -> it1() } }
                menuView.visibility = View.VISIBLE
            }else {
                menuView.visibility = View.GONE
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pipModeView.visibility = View.VISIBLE
            }else {
                pipModeView.visibility = View.GONE
            }

            pipModeView.setOnClickListener { view ->
                pipMode(
                    context = view.context,
                    playerView = playerView
                )
            }

            playerView(
                playerView = playerView,
                progressBar = progressBar,
                exoPlayer = exoPlayer,
                parameters = parameters
            )

            exoProgress(
                progressView = exoProgressBar,
                playerView = playerView,
                parameters = parameters
            )

            videoTitleDescription(
                titleView = titleView,
                descriptionView = descriptionView,
                parameters = parameters
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

@ExperimentalMaterialApi
private fun playerView(
    playerView:StyledPlayerView,
    progressBar: ProgressBar,
    exoPlayer:ExoPlayer,
    parameters: ExoCustomParameters
){
    playerView.player = exoPlayer
    playerView.useController = parameters.useController

    playerView.resizeMode = parameters.resizeMode

    playerView.layoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    playerView.player?.addListener(object: Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING){
                progressBar.visibility = View.VISIBLE
                parameters.onProgressBarVisibility(true)
            } else if (playbackState == Player.STATE_READY){
                progressBar.visibility = View.GONE
                parameters.onProgressBarVisibility(false)
            }
        }
    })
}

@ExperimentalMaterialApi
private fun exoPlayer(
    exoPlayer: ExoPlayer,
    parameters: ExoCustomParameters,
    url:String
){
    exoPlayer.apply {
        setMediaItem(MediaItem.fromUri(url))
        prepare()

        when(parameters.statePlayPause){
            VideoPlayerPausePlayState.PAUSE -> pause()
            VideoPlayerPausePlayState.PLAY -> play()
        }
    }
}

@ExperimentalMaterialApi
private fun videoTitleDescription(
    titleView:TextView,
    descriptionView:TextView,
    parameters: ExoCustomParameters
){
    titleView.text = parameters.title
    descriptionView.text = parameters.description

    titleView.setOnClickListener { parameters.onTitleClick() }
    descriptionView.setOnClickListener { parameters.onDescriptionClick() }
}

@ExperimentalMaterialApi
@SuppressLint("ResourceAsColor")
private fun exoProgress(
    progressView:DefaultTimeBar,
    playerView:StyledPlayerView,
    parameters: ExoCustomParameters
){
    progressView.setUnplayedColor(parameters.progressBarUnplacedColor)
    progressView.setBufferedColor(parameters.progressBarBufferedColor)
    progressView.setScrubberColor(parameters.progressBarScrubberColor)
    progressView.setPlayedColor(parameters.progressBarPlayedColor)

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

@ExperimentalMaterialApi
private fun nextPreviousButton(
    videoList:List<VideoExoPlayer>,
    exoPlayer: ExoPlayer,
    titleView:TextView,
    descriptionView:TextView,
    bottomNextView:ImageView,
    bottomPreviousView:ImageView,
    parameters: ExoCustomParameters
){
    when(parameters.nextPreviousButtonPosition){
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
                    isNext = false,
                    exoPlayer = exoPlayer,
                    titleView = titleView,
                    descriptionView = descriptionView,
                    parameters = parameters
                )
            }

            bottomNextView.setOnClickListener {
                nextButton(
                    videoList = videoList,
                    isNext = true,
                    exoPlayer = exoPlayer,
                    titleView = titleView,
                    descriptionView = descriptionView,
                    parameters = parameters
                )
            }
        }
        NextPreviousButtonPosition.NO_ELEMENTS -> {
            bottomNextView.visibility = View.GONE
            bottomPreviousView.visibility = View.GONE
        }
    }
}

@ExperimentalMaterialApi
private fun nextButton(
    videoList: List<VideoExoPlayer>,
    exoPlayer: ExoPlayer,
    titleView:TextView,
    descriptionView:TextView,
    isNext:Boolean = true,
    parameters: ExoCustomParameters
){
    if (isNext){
        setPosition(
            videoList = videoList,
            positionButton = parameters.nextPreviousButtonPosition
        )
    }else{
        if (position != 0){
            setPosition(
                videoList = videoList,
                positionButton = parameters.nextPreviousButtonPosition,
                isIncrement = false
            )
        }
    }

    createPlayer(
        videoList = videoList,
        exoPlayer = exoPlayer,
        titleView = titleView,
        descriptionView = descriptionView,
        parameters = parameters
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

@ExperimentalMaterialApi
private fun createPlayer(
    videoList: List<VideoExoPlayer>,
    exoPlayer: ExoPlayer,
    titleView:TextView,
    descriptionView:TextView,
    parameters: ExoCustomParameters
){
    exoPlayer.setMediaItem(MediaItem.fromUri(videoList[position].videoUrl))
    titleView.text = videoList[position].title
    descriptionView.text = videoList[position].description
    exoPlayer.prepare()

    when(parameters.statePlayPause){
        VideoPlayerPausePlayState.PAUSE -> exoPlayer.play()
        VideoPlayerPausePlayState.PLAY -> exoPlayer.pause()
    }
}

@ExperimentalMaterialApi
private fun repeat(
    exoPlayer: ExoPlayer,
    videoList: List<VideoExoPlayer>,
    titleView: TextView,
    descriptionView: TextView,
    parameters: ExoCustomParameters
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
                    isNext = true,
                    parameters = parameters
                )
            }
        }
    })
}

private fun audioTrack(
    player: ExoPlayer,
    audioTrackView:ImageView? = null
){
    audioTrackView?.let {
        if (audioTrack.isNotEmpty()){
            audioTrackView.visibility = View.VISIBLE
        }else{
            audioTrackView.visibility = View.GONE
        }
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
){ player.setPlaybackSpeed(speed) }

private fun pipMode(
    context: Context,
    playerView: StyledPlayerView
){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val activity = context as Activity
        activity.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        playerView.hideController()
    }
}