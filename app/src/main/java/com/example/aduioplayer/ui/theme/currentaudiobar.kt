package com.example.aduioplayer.ui.theme

import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.aduioplayer.R
import com.example.aduioplayer.TracksViewModel
import com.example.aduioplayer.frservice.Actions
import com.example.aduioplayer.frservice.AudioForeGroundService

@Composable
fun  AudioBottomBar(
    mediaPlayer: MediaPlayer, tracksViewModel: TracksViewModel,
) {
    val isPlaying = tracksViewModel.isPlaying.collectAsState()
    val currentTrack = tracksViewModel.currentTrack.collectAsState()
    val localContext = LocalContext.current
    BottomAppBar {
         Text(text = currentTrack.value.name, modifier = Modifier.width(200.dp))
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(onClick = {
            Intent(localContext.applicationContext,AudioForeGroundService::class.java).also {
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
            Log.d("audio playing",isPlaying.toString())
            if (isPlaying.value) {
                Intent(localContext.applicationContext,AudioForeGroundService::class.java).also {
                    it.action = Actions.Pause.name
                    localContext.startService(it)
                }
            } else {
                Intent(localContext.applicationContext,AudioForeGroundService::class.java).also {
                    it.action = Actions.Play.name
                    localContext.startService(it)
                }
                Log.d("audio","${mediaPlayer.isPlaying}")
            }
        }) {
            if (isPlaying.value) {
                Icon(
                    painter = painterResource(id = R.drawable.pause), contentDescription = "",
            modifier = Modifier.size(30.dp)) }
            else{ Icon(
                        imageVector = Icons.Filled.PlayArrow,
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )}
        }
        IconButton(onClick = {
            Intent(localContext.applicationContext, AudioForeGroundService::class.java).also {
                it.action = Actions.NEXT.name
                localContext.startService(it)
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.play_button), contentDescription = "",
                modifier = Modifier.size(30.dp)
            )
        }
        Column{
            IconButton(onClick = {       Intent(localContext.applicationContext, AudioForeGroundService::class.java).also {
                it.action = Actions.Cancel.name
                localContext.startService(it)
            } }) {
                Icon(imageVector = Icons.Filled.Cancel, contentDescription = "Stop")
            }
        }

    }
}