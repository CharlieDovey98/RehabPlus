package com.charliedovey.rehabplus.ui.components

// Import necessary libraries for Jetpack Compose.
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.DisposableEffect
// Import libraries needed for Android ExoPlayer video playing service.
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * VideoPlayer is a composable component that displays an exercise video using Android’s embedded ExoPlayer.
 * This component streams videos from Azure Blob Storage,
 * and is used within the detailed exercise screen to allow users to view demonstration videos.
 */

// A reusable Composable function that streams a video using ExoPlayer.
@Composable
fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Create the ExoPlayer instance.
    val exoPlayer = ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(videoUrl.toUri())) // Set the Azure video URL.
        prepare() // Buffer the video.
        playWhenReady = false // Don't auto-play the exercise video.
        volume = 0f // Mute the video.
    }

    // Release the player when this composable is removed, safe removal.
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Embed Android PlayerView inside Compose.
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true // Show play and pause UI buttons.
            }
        },
        modifier = modifier
    )
}
