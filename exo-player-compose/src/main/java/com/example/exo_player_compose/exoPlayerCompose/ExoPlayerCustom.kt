package com.example.exo_player_compose.exoPlayerCompose

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.exo_player_compose.R
import com.example.exo_player_compose.doubleTapPlayerView.DoubleTapPlayerView
import com.example.exo_player_compose.doubleTapPlayerView.youtube.YouTubeOverlay
import com.example.exo_player_compose.exoPlayerCompose.dialogs.AudioTrackDialog
import com.example.exo_player_compose.exoPlayerCompose.dialogs.SpeedDialog
import com.example.exo_player_compose.exoPlayerCompose.model.ExoCustomParameters
import com.example.exo_player_compose.exoPlayerCompose.model.VideoExoPlayer
import com.example.exo_player_compose.exoPlayerCompose.state.NextPreviousButtonPosition
import com.example.exo_player_compose.exoPlayerCompose.state.VideoPlayerPausePlayState
import com.example.exo_player_compose.exoPlayerCompose.type.ExoFilterType
import com.example.exo_player_compose.exoPlayerFilter.EPlayerView
import com.example.exo_player_compose.previewSeekBar.PreviewSeekBarAnimationType
import com.example.exo_player_compose.previewSeekBar.animator.PreviewFadeAnimator
import com.example.exo_player_compose.previewSeekBar.animator.PreviewMorphAnimator
import com.example.exo_player_compose.previewSeekBar.exoplayer.PreviewExoSeekBar
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TimeBar
import java.util.*

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
    parameters: ExoCustomParameters,
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

@ExperimentalMaterialApi
@Composable
private fun ExoPlayerAndroidView(
    modifier: Modifier = Modifier,
    videoList: List<VideoExoPlayer>,
    parameters: ExoCustomParameters,
    exoPlayer: ExoPlayer,
    onFullscreen: (Boolean) -> Unit,
    onAudioTrackClick: () -> Unit,
    onSpeedClick: () -> Unit,
){
    AndroidView(
        modifier = modifier,
        factory = {
            View.inflate(it, R.layout.custom_styled_player, null)
        },
        update = {
            val frameLayoutExoPlayer = it.findViewById<FrameLayout>(R.id.frame_layout_exo_player)

            val playerView = it.findViewById<DoubleTapPlayerView>(R.id.player_view)
            val progressBar = it.findViewById<ProgressBar>(R.id.progress_bar)
            val exoProgressBar = it.findViewById<PreviewExoSeekBar>(R.id.exo_progress)
            val titleView = it.findViewById<TextView>(R.id.video_title)
            val descriptionView = it.findViewById<TextView>(R.id.video_description)

            val bottomNextView = it.findViewById<ImageView>(R.id.bottom_skip_next)
            val bottomPreviousView = it.findViewById<ImageView>(R.id.bottom_skip_previous)

            val exoRew = it.findViewById<ImageView>(R.id.exo_rew)
            val exoFfWd = it.findViewById<ImageView>(R.id.exo_ffwd)

            val fullscreenView = it.findViewById<ImageView>(R.id.fullscreen)
            val audioTrackView = it.findViewById<ImageView>(R.id.audio_track)
            val menuView = it.findViewById<ImageView>(R.id.menu)
            val subtitleView = it.findViewById<ImageView>(R.id.subtitles)
            val speedView = it.findViewById<ImageView>(R.id.speed)
            val pipModeView = it.findViewById<ImageView>(R.id.pip_mode)

            val previewImageView = it.findViewById<ImageView>(R.id.preview_image_view)

            val youtubeOverlay = it.findViewById<YouTubeOverlay>(R.id.youtube_overlay)

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

            when(parameters.doubleTapParameters.enabled){
                true -> {
                    exoRew.visibility = View.GONE
                    exoFfWd.visibility = View.GONE

                    doubleTapEnable(
                        youtubeOverlay = youtubeOverlay,
                        exoPlayer = exoPlayer,
                        parameters = parameters
                    )
                }
                false -> {
                    exoRew.visibility = View.VISIBLE
                    exoFfWd.visibility = View.VISIBLE
                }
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

            if (parameters.previewSeekBarParameters.enabled){
                exoProgressBar.isPreviewEnabled = true
                previewExoSeekBar(
                    previewImageView = previewImageView,
                    previewExoBar = exoProgressBar,
                    player = exoPlayer,
                    parameters = parameters
                )
            }else {
                exoProgressBar.isPreviewEnabled = false
                exoProgress(
                    progressView = exoProgressBar,
                    playerView = playerView,
                    parameters = parameters
                )
            }


            nextPreviousButton(
                videoList = videoList,
                exoPlayer = exoPlayer,
                titleView = titleView,
                descriptionView = descriptionView,
                bottomNextView = bottomNextView,
                bottomPreviousView = bottomPreviousView,
                parameters = parameters
            )

            videoFilter(
                player = exoPlayer,
                frameLayoutExoPlayer = frameLayoutExoPlayer,
                parameters = parameters,
                context = it.context
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
    onAudioTrackClick: () -> Unit,
    onSpeedClick: () -> Unit,
){
    AndroidView(
        modifier = modifier,
        factory = {
            View.inflate(it, R.layout.custom_styled_player, null)
        },
        update = {
            val frameLayoutExoPlayer = it.findViewById<FrameLayout>(R.id.frame_layout_exo_player)

            val playerView = it.findViewById<DoubleTapPlayerView>(R.id.player_view)
            val progressBar = it.findViewById<ProgressBar>(R.id.progress_bar)
            val exoProgressBar = it.findViewById<PreviewExoSeekBar>(R.id.exo_progress)
            val titleView = it.findViewById<TextView>(R.id.video_title)
            val descriptionView = it.findViewById<TextView>(R.id.video_description)

            val exoRew = it.findViewById<ImageView>(R.id.exo_rew)
            val exoFfWd = it.findViewById<ImageView>(R.id.exo_ffwd)

            val fullscreenView = it.findViewById<ImageView>(R.id.fullscreen)

            val menuView = it.findViewById<ImageView>(R.id.menu)
            val audioTrackView = it.findViewById<ImageView>(R.id.audio_track)
            val subtitleView = it.findViewById<ImageView>(R.id.subtitles)
            val speedView = it.findViewById<ImageView>(R.id.speed)
            val pipModeView = it.findViewById<ImageView>(R.id.pip_mode)

            val previewImageView = it.findViewById<ImageView>(R.id.preview_image_view)

            val youtubeOverlay = it.findViewById<YouTubeOverlay>(R.id.youtube_overlay)

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

            when(parameters.doubleTapParameters.enabled){
                true -> {
                    exoRew.visibility = View.GONE
                    exoFfWd.visibility = View.GONE

                    doubleTapEnable(
                        youtubeOverlay = youtubeOverlay,
                        exoPlayer = exoPlayer,
                        parameters = parameters
                    )
                }
                false -> {
                    exoRew.visibility = View.VISIBLE
                    exoFfWd.visibility = View.VISIBLE
                }
            }

            playerView(
                playerView = playerView,
                progressBar = progressBar,
                exoPlayer = exoPlayer,
                parameters = parameters
            )

            if (parameters.previewSeekBarParameters.enabled){
                exoProgressBar.isPreviewEnabled = true
                previewExoSeekBar(
                    previewImageView = previewImageView,
                    previewExoBar = exoProgressBar,
                    player = exoPlayer,
                    parameters = parameters
                )
            }else {
                exoProgressBar.isPreviewEnabled = false
                exoProgress(
                    progressView = exoProgressBar,
                    playerView = playerView,
                    parameters = parameters
                )
            }

            videoFilter(
                player = exoPlayer,
                frameLayoutExoPlayer = frameLayoutExoPlayer,
                parameters = parameters,
                context = it.context
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
    playerView: DoubleTapPlayerView,
    progressBar: ProgressBar,
    exoPlayer: ExoPlayer,
    parameters: ExoCustomParameters,
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
    url: String,
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
    titleView: TextView,
    descriptionView: TextView,
    parameters: ExoCustomParameters,
){
    titleView.text = parameters.title
    descriptionView.text = parameters.description

    titleView.setOnClickListener { parameters.onTitleClick() }
    descriptionView.setOnClickListener { parameters.onDescriptionClick() }
}

@ExperimentalMaterialApi
@SuppressLint("ResourceAsColor")
private fun exoProgress(
    progressView: DefaultTimeBar,
    playerView: DoubleTapPlayerView,
    parameters: ExoCustomParameters,
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
    videoList: List<VideoExoPlayer>,
    exoPlayer: ExoPlayer,
    titleView: TextView,
    descriptionView: TextView,
    bottomNextView: ImageView,
    bottomPreviousView: ImageView,
    parameters: ExoCustomParameters,
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
    titleView: TextView,
    descriptionView: TextView,
    isNext: Boolean = true,
    parameters: ExoCustomParameters,
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
    isIncrement: Boolean = true,
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
    titleView: TextView,
    descriptionView: TextView,
    parameters: ExoCustomParameters,
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
    parameters: ExoCustomParameters,
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
    audioTrackView: ImageView? = null,
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
    subtitleView: ImageView,
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
    player: ExoPlayer,
){ player.setPlaybackSpeed(speed) }

private fun pipMode(
    context: Context,
    playerView: DoubleTapPlayerView,
){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val activity = context as Activity
        activity.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        playerView.hideController()
    }
}

@ExperimentalMaterialApi
private fun doubleTapEnable(
    youtubeOverlay: YouTubeOverlay,
    exoPlayer: ExoPlayer,
    parameters: ExoCustomParameters,
){
    youtubeOverlay.seekSeconds(parameters.doubleTapParameters.seekSeconds)
    youtubeOverlay.animationDuration(parameters.doubleTapParameters.animationDuration)
    youtubeOverlay.arcSize(parameters.doubleTapParameters.arcSize)
    youtubeOverlay.iconAnimationDuration(parameters.doubleTapParameters.iconAnimationDuration)

    parameters.doubleTapParameters.textAppearance?.let {
        youtubeOverlay.textAppearance(it)
    }

    parameters.doubleTapParameters.icon?.let {
        youtubeOverlay.icon(it)
    }

    parameters.doubleTapParameters.backgroundCircleColor?.let {
        youtubeOverlay.circleBackgroundColorRes(it)
    }

    parameters.doubleTapParameters.tapCircleColor?.let {
        youtubeOverlay.tapCircleColorRes(it)
    }

    youtubeOverlay.performListener(object : YouTubeOverlay.PerformListener {
        override fun onAnimationStart() {
            youtubeOverlay.visibility = View.VISIBLE
        }

        override fun onAnimationEnd() {
            youtubeOverlay.visibility = View.GONE
        }
    })

    youtubeOverlay.player(exoPlayer)
}

@SuppressLint("ObsoleteSdkInt")
@ExperimentalMaterialApi
private fun previewExoSeekBar(
    previewImageView: ImageView,
    previewExoBar: PreviewExoSeekBar,
    player: ExoPlayer,
    parameters: ExoCustomParameters,
){
    val exoPlayerManager = ExoPlayerManager(
        previewUrl = parameters.previewSeekBarParameters.previewUrl,
        previewImageView = previewImageView,
        player = player
    )

    parameters.previewSeekBarParameters.apply {
        exoPlayerManager.setResumeVideoOnPreviewStop(previewAutoHideSwitch)
        previewExoBar.setAutoHidePreview(previewAutoHideSwitch)
        previewExoBar.setPreviewAnimationEnabled(previewAnimationEnabled)

        when(typeAnimation){
            PreviewSeekBarAnimationType.MORPH_ANIMATION -> {
                previewExoBar.setPreviewAnimationEnabled(true)
                if (Build.VERSION.SDK_INT >= 21) {
                    previewExoBar.setPreviewAnimator(PreviewMorphAnimator())
                }
            }
            PreviewSeekBarAnimationType.DISABLE_ANIMATION -> {
                previewExoBar.setPreviewAnimationEnabled(false)
            }
            PreviewSeekBarAnimationType.FADE_ANIMATION -> {
                previewExoBar.setPreviewAnimationEnabled(true)
                if (Build.VERSION.SDK_INT >= 21) {
                    previewExoBar.setPreviewAnimator(PreviewMorphAnimator())
                } else {
                    previewExoBar.setPreviewAnimator(PreviewFadeAnimator())
                }
            }
        }
    }

    previewExoBar.setPreviewLoader(exoPlayerManager)
    previewExoBar.addOnScrubListener(exoPlayerManager)
}

@ExperimentalMaterialApi
private fun videoFilter(
    player: ExoPlayer,
    frameLayoutExoPlayer:FrameLayout,
    parameters: ExoCustomParameters,
    context: Context
){
    if(parameters.exoFilterParameters.type != ExoFilterType.DEFAULT){
        val ePlayerView = EPlayerView(context)

        ePlayerView.apply {
            setSimpleExoPlayer(player)
            setGlFilter(ExoFilterType.createGlFilter(parameters.exoFilterParameters.type, context))
            layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            frameLayoutExoPlayer.addView(ePlayerView, 0)
            onResume()
        }
    }
}