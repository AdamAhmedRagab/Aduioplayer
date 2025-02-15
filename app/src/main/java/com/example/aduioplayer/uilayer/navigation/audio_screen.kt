package com.example.aduioplayer.uilayer.navigation

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aduioplayer.applicationLayer.PlayListViewModel
import com.example.aduioplayer.applicationLayer.TracksViewModel
import com.example.aduioplayer.frservice.Actions
import com.example.aduioplayer.frservice.AudioForeGroundService
import com.example.aduioplayer.ui.theme.Audio
import com.example.aduioplayer.ui.theme.AudioTrack
import com.example.aduioplayer.ui.theme.DropDownActions

@Composable
fun <T : Audio> AudiosScreen(
    audioList: List<T>,
    tracksViewModel: TracksViewModel,
    playListViewModel: PlayListViewModel,
    dropDownActions: List<DropDownActions>,
    navController: NavController
) {
    val context = LocalContext.current
    Scaffold(topBar = {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {
                if (tracksViewModel.currentTrackList.value
                    != audioList
                ) {
                    tracksViewModel.newCurrentTrackList(audioList)
                }
                Intent(
                    context.applicationContext,
                    AudioForeGroundService::class.java
                ).also {
                    it.action = Actions.PlayList.name
                    context.startService(it)
                }
            }, Modifier.wrapContentWidth(Alignment.End)) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "")
            }
            IconButton(onClick = {
                if (tracksViewModel.currentTrackList.value
                    != audioList
                ) {
                    tracksViewModel.newCurrentTrackList(audioList)
                }
                Intent(
                    context.applicationContext,
                    AudioForeGroundService::class.java
                ).also {
                    it.action = Actions.Shuffle.name
                    context.startService(it)
                }
            }, modifier = Modifier.wrapContentWidth(Alignment.End)) {
                Icon(imageVector = Icons.Filled.Shuffle, contentDescription = "")
            }
        }
    }) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = it) {
            items(audioList, key = { it.id }) {
                AudioTrackCard(
                    audioTrack = AudioTrack(
                        it.name, it.id, it.uri, it.length
                    ),
                    onClick = {
                        if (tracksViewModel.currentTrackList.value
                            != audioList
                        ) {
                            tracksViewModel.newCurrentTrackList(audioList)
                        }
                        tracksViewModel.newCurrentTrack(
                            AudioTrack(
                                it.name,
                                it.id,
                                it.uri,
                                it.length
                            )
                        )
                        Intent(
                            context.applicationContext,
                            AudioForeGroundService::class.java
                        ).also {
                            it.action = Actions.Select.name
                            Log.d("audio foreground service intent action:", it.action ?: "ssa")
                            context.startService(it)
                        }
                        navController.navigate(Screens.TrackScreen.name)

                    }, dropDownActions,
                    playListViewModel = playListViewModel
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioTrackCard(
    audioTrack: AudioTrack,
    onClick: () -> Unit,
    dropDownActions: List<DropDownActions>,
    playListViewModel: PlayListViewModel
) {
    val maxWidth = LocalConfiguration.current
    val localContext = LocalContext.current
    var isOpen by rememberSaveable {
        mutableStateOf(false)
    }
    Box {
        var modalSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(
                    horizontal = 10.dp,
                    vertical = 10.dp
                )
                .clickable {
                    onClick()
                }, elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {

            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    )
                    .fillMaxSize()
            ) {
                Column(Modifier.width(300.dp)) {
                    Text(
                        text = audioTrack.name,
                        style = MaterialTheme.typography.titleSmall,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                IconButton(
                    onClick = { isOpen = true },
                    modifier = Modifier.wrapContentWidth(align = Alignment.End)
                ) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "")

                }

            }

            DropdownMenu(
                expanded = isOpen,
                onDismissRequest = { isOpen = false },
                offset = DpOffset(x = maxWidth.screenWidthDp.dp, y = 0.dp)
            ) {
                DropdownMenuItem(text = { Text(text = "Add") }, onClick = { modalSheetOpen = true })
                dropDownActions.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.name) },
                        onClick = {
                            it.action.invoke(
                                audioTrack
                            )
                        })
                }

            }

        }

        if (modalSheetOpen) {
            ModalBottomSheet(onDismissRequest = { isOpen = false;modalSheetOpen = false }) {
                Column {
                    val playlists = playListViewModel.playLists.collectAsState(initial = listOf())
                    playlists.value.forEach {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clickable {
                                    playListViewModel.addToPLayList(
                                        audioTrack,
                                        it.id
                                    )
                                    Toast
                                        .makeText(
                                            localContext,
                                            "added succesfully",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = it.name)
                            IconButton(onClick = {
                                playListViewModel.addToPLayList(
                                    audioTrack,
                                    it.id
                                )
                                Toast.makeText(
                                    localContext,
                                    "added succesfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}
