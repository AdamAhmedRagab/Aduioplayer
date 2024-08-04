package com.example.aduioplayer.uilayer.navigation

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@Composable
fun AudioBottomBar(
    tracksViewModel: TracksViewModel,
    navController: NavController
) {
    val isPlaying = tracksViewModel.isPlaying.collectAsState()
    val currentTrack = tracksViewModel.currentTrack.collectAsState()
    val localContext = LocalContext.current
    BottomAppBar(Modifier.clickable { navController.navigate(Screens.TrackScreen.name) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = currentTrack.value.name, Modifier.weight(1f))
            IconButton(onClick = {
                Intent(localContext.applicationContext, AudioForeGroundService::class.java).also {
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
                Log.d("audio playing", isPlaying.toString())
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
                }
            }) {
                if (isPlaying.value) {
                    Icon(
                        painter = painterResource(id = R.drawable.pause), contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                    )
                }
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
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopEnd
            ) {

                IconButton(onClick = {
                    Intent(
                        localContext.applicationContext,
                        AudioForeGroundService::class.java
                    ).also {
                        it.action = Actions.Cancel.name
                        localContext.startService(it)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = "Stop",
                    )
                }
            }
        }

    }
}