package com.aizentv.app

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : Activity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaUrl = intent.getStringExtra(EXTRA_MEDIA_URL)
        if (mediaUrl.isNullOrBlank()) {
            finish()
            return
        }

        val uri = Uri.parse(mediaUrl)
        if (uri.scheme !in listOf("https", "content")) {
            Toast.makeText(
                this,
                "Fonte de vídeo não permitida.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        playerView = PlayerView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = true
        }

        setContentView(
            FrameLayout(this).apply {
                addView(playerView)
            }
        )

        val exoPlayer = ExoPlayer.Builder(this).build()
        player = exoPlayer
        playerView.player = exoPlayer

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(
                    this@PlayerActivity,
                    "Não foi possível reproduzir este conteúdo.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        playerView.player = null
        player?.release()
        player = null
    }

    companion object {
        const val EXTRA_MEDIA_URL = "aizen_media_url"
        const val EXTRA_TITLE = "aizen_media_title"
    }
}
