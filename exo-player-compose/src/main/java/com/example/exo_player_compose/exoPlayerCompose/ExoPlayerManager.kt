package com.example.exo_player_compose.exoPlayerCompose

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.exo_player_compose.previewSeekBar.PreviewBar
import com.example.exo_player_compose.previewSeekBar.PreviewLoader
import com.example.exo_player_compose.previewSeekBar.glide.GlideThumbnailTransformation
import com.google.android.exoplayer2.ExoPlayer

internal class ExoPlayerManager(
    private val previewUrl: String,
    private val previewImageView: ImageView,
    private val player: ExoPlayer,
) : PreviewLoader, PreviewBar.OnScrubListener {

    private var resumeVideoOnPreviewStop = true

    override fun onScrubStart(previewBar: PreviewBar?) {
        player.playWhenReady = false
    }

    override fun onScrubMove(previewBar: PreviewBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onScrubStop(previewBar: PreviewBar?) {
        if (resumeVideoOnPreviewStop) {
            player.playWhenReady = true
        }
    }

    override fun loadPreview(currentPosition: Long, max: Long) {
        if (player.isPlaying) {
            player.playWhenReady = false
        }

        Glide.with(previewImageView)
            .load(previewUrl)
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .transform(GlideThumbnailTransformation(currentPosition))
            .into(previewImageView)
    }

    fun setResumeVideoOnPreviewStop(resume: Boolean) {
        this.resumeVideoOnPreviewStop = resume
    }
}