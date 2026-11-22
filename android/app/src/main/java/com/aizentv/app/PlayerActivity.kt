package com.aizentv.app

import android.graphics.Color

import android.content.Intent
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import com.google.firebase.auth.FirebaseAuth

class PlayerActivity : Activity() {

    private var player: ExoPlayer? = null
    private var mediaId: String? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkTheme = AizenPreferences.isDark(this)
        val barColor = if (darkTheme) Color.rgb(8, 8, 13) else Color.rgb(246, 244, 250)
        window.statusBarColor = barColor
        window.navigationBarColor = barColor

        if (FirebaseAuth.getInstance().currentUser?.isEmailVerified != true) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        mediaId = intent.getStringExtra(EXTRA_MEDIA_ID)

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

        val subtitleLanguage = AizenPreferences.subtitleLanguage(this)

        val trackSelector = DefaultTrackSelector(this).apply {
            parameters = buildUponParameters()
                .setTrackTypeDisabled(
                    C.TRACK_TYPE_TEXT,
                    subtitleLanguage == "off"
                )
                .apply {
                    if (subtitleLanguage != "off") {
                        setPreferredTextLanguages(subtitleLanguage)
                    }
                }
                .build()
        }

        val exoPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
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

        val previousProgress = mediaId
            ?.takeIf { it.isNotBlank() }
            ?.let { id -> LibraryStore.getProgress(this, id) }

        exoPlayer.setMediaItem(MediaItem.fromUri(uri))

        if (previousProgress != null && previousProgress.positionMs > 0L) {
            exoPlayer.seekTo(previousProgress.positionMs)
        }

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun onStop() {
        val id = mediaId
        val activePlayer = player

        if (!id.isNullOrBlank() && activePlayer != null) {
            LibraryStore.saveProgress(
                this,
                id,
                activePlayer.currentPosition,
                activePlayer.duration.coerceAtLeast(0L)
            )
        }

        if (::playerView.isInitialized) {
            playerView.player = null
        }

        player?.release()
        player = null
        super.onStop()
    }
    companion object {
        const val EXTRA_MEDIA_URL = "aizen_media_url"
        const val EXTRA_TITLE = "aizen_media_title"
        const val EXTRA_MEDIA_ID = "aizen_media_id"
    }
}
