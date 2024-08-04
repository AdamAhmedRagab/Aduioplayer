package com.example.aduioplayer.uilayer.navigation

import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aduioplayer.R
import com.example.aduioplayer.applicationLayer.TracksViewModel
import com.example.aduioplayer.frservice.Actions
import com.example.aduioplayer.frservice.AudioForeGroundService
import kotlinx.coroutines.delay

@Composable
fun TrackScreen(
    modifier: Modifier = Modifier,
    tracksListViewModel: TracksViewModel,
    mediaPlayer: MediaPlayer,
    navController: NavController,
) {
    val isPlaying = tracksListViewModel.isPlaying.collectAsState()
    val currentTrack = tracksListViewModel.currentTrack.collectAsState()

    // Remember the current position and duration of the track
    val currentPosition = remember { mutableFloatStateOf(0f) }
    val duration = remember { mutableFloatStateOf(0f) }

    // Update the duration when the track changes
    LaunchedEffect(currentTrack.value, true) {
        duration.floatValue = mediaPlayer.duration.toFloat()
    }
    // Periodically update the current position of the track
    LaunchedEffect(isPlaying.value, key2 = true) {
        while (isPlaying.value) {
            currentPosition.floatValue = mediaPlayer.currentPosition.toFloat()
            delay(1000)  // Update every second
        }
    }

    val localContext = LocalContext.current
    Scaffold(topBar = {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }
    }) {
        Column(modifier.padding(it)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector =
                    Icons.Filled.Headset, contentDescription = "", Modifier.size(150.dp)
                )
            }
            Text(text = currentTrack.value.name)
            Slider(
                value = currentPosition.floatValue,
                onValueChange = {
                    currentPosition.floatValue = it
                    mediaPlayer.seekTo(it.toInt())
                },
                valueRange = 0f..duration.floatValue
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = (currentPosition.floatValue / 1000).toInt().toString())

                Text(text = (duration.floatValue / 1000).toInt().toString())
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    Intent(
                        localContext.applicationContext,
                        AudioForeGroundService::class.java
                    ).also {
                        it.action = Actions.Loop.name
                        localContext.startService(it)
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Loop, contentDescription = "Loop")
                }
                IconButton(onClick = {
                    Intent(
                        localContext.applicationContext,
                        AudioForeGroundService::class.java
                    ).also {
                        it.action = Actions.AutoNext.name
                        localContext.startService(it)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Auto Next Audio"
                    )
                }
                IconButton(onClick = {
                    Intent(
                        localContext.applicationContext,
                        AudioForeGroundService::class.java
                    ).also {
                        it.action = Actions.PREV.name
                        localContext.startService(it)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.music_play_button),
                        contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                }
                IconButton(onClick = {
                    if (isPlaying.value) {
                        Intent(
                            localContext.applicationContext,
                            AudioForeGroundService::class.java
                        ).also {
                            it.action = Actions.Pause.name
                            localContext.startService(it)
                        }
                    } else {
                        Intent(
                            localContext.applicationContext,
                            AudioForeGroundService::class.java
                        ).also {
                            it.action = Actions.Play.name
                            localContext.startService(it)
                        }
                        Log.d("audio", "${mediaPlayer.isPlaying}")
                    }
                }) {
                    if (isPlaying.value) {
                        Icon(
                            painter = painterResource(id = R.drawable.pause),
                            contentDescription = "",
                            modifier = Modifier.size(50.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
                IconButton(onClick = {
                    Intent(
                        localContext.applicationContext,
                        AudioForeGroundService::class.java
                    ).also {
                        it.action = Actions.NEXT.name
                        localContext.startService(it)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.play_button),
                        contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}