package com.example.aduioplayer.ui.theme

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aduioplayer.R
import com.example.aduioplayer.TracksViewModel
import com.example.aduioplayer.frservice.Actions
import com.example.aduioplayer.frservice.AudioForeGroundService

@Composable
fun TrackScreen(
    modifier: Modifier = Modifier,
    tracksListViewModel: TracksViewModel,
    mediaPlayer: MediaPlayer,
    navController: NavController,
    audioList: List<Audio>,
    tracksViewModel: TracksViewModel,

) {
    val isPlaying = tracksViewModel.isPlaying.collectAsState()
    val currentTrack = tracksListViewModel.currentTrack.collectAsState()
    var currentTime by remember {
        mutableFloatStateOf(0f)
    }
    currentTime = 0f
    LaunchedEffect(key1 = currentTime) {
        currentTime = mediaPlayer.duration.toFloat()
    }
    val localContext = LocalContext.current
    Scaffold(topBar = {
        IconButton(onClick = { navController.navigateUp() }) {
        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
    }
    }){
        Column(modifier.padding(it)) {
            Icon(imageVector = Icons.Filled.Headset, contentDescription = "", Modifier.size(300.dp))
            Text(text = currentTrack.value.name)
            Slider(value = currentTime, onValueChange = {
                currentTime = it
                mediaPlayer.seekTo(it.toInt())
            })
            Row (horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = currentTime.toString())
                Text(text = mediaPlayer.duration.toString())
            }
            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                IconButton(onClick = {
                    Intent(Actions.PREV.name).also {
                        it.setPackage(localContext.packageName)
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
                        Intent(localContext.applicationContext, AudioForeGroundService::class.java).also {
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
                    Intent(Actions.Play.name).also {
                        it.setPackage(localContext.packageName)
                        localContext.startService(it)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.play_button), contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

    }
}