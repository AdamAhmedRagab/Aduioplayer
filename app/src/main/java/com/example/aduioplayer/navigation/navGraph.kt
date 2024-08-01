package com.example.aduioplayer.navigation
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aduioplayer.TracksViewModel
import com.example.aduioplayer.frservice.Actions
import com.example.aduioplayer.frservice.AudioForeGroundService
import com.example.aduioplayer.ui.theme.Audio
import com.example.aduioplayer.ui.theme.AudioBottomBar
import com.example.aduioplayer.ui.theme.AudioTrack
import com.example.aduioplayer.ui.theme.AudioTrackEntity
import com.example.aduioplayer.ui.theme.AudiosScreen
import com.example.aduioplayer.ui.theme.DropDownActions
import com.example.aduioplayer.ui.theme.PlayListScreen
import com.example.aduioplayer.ui.theme.PlayListViewModel
import com.example.aduioplayer.ui.theme.TrackScreen
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Nav() {
    val localContext = LocalContext.current
    val audioViewModel = viewModel<TracksViewModel>()
    val playListViewModel = viewModel<PlayListViewModel>()
    val navController = rememberNavController()
    val mediaPlayer = MediaPlayer()
    val currentTrack = audioViewModel.currentTrack.collectAsState()
    NavHost(navController = navController, startDestination = Screens.MainScreen.name) {
        composable(route = Screens.MainScreen.name) {
            var selectedIndex by remember {
                mutableIntStateOf(0)
            }
            val pagerState = rememberPagerState(0) {
                2
            }
            LaunchedEffect(key1 = selectedIndex) {
                pagerState.animateScrollToPage(selectedIndex)

            }
            LaunchedEffect(key1 = pagerState.currentPage) {
                selectedIndex = pagerState.currentPage
            }
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(topBar = {
                    TabRow(selectedTabIndex = selectedIndex) {
                        listOf("Audios", "PlayList").forEachIndexed { index, it ->
                            Tab(
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                text = {
                                    Text(
                                        text = it
                                    )
                                })
                        }
                    }
                }, bottomBar = {
                    AnimatedVisibility(visible = currentTrack.value.uri != Uri.EMPTY) {
                        AudioBottomBar(
                            mediaPlayer,
                            audioViewModel,
                        )
                    }
                })
                { paddingValues ->

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = paddingValues,
                        key = { it }
                    ) { index ->
                        if (index == 0) {
                            audioViewModel.newCurrentTrackList(audioViewModel.tracksList)
                            Box {
                                AudiosScreen<Audio>(
                                    audioList = audioViewModel.tracksList,
                                    mediaPlayer,
                                    tracksViewModel = audioViewModel,
                                    playListViewModel,
                                    listOf(DropDownActions("Share") {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = it.uri
                                        }
                                        if (intent.resolveActivity(localContext.packageManager) != null) {
                                            localContext.startActivity(intent)
                                        }
                                    }),
                                )

                            }
                        } else {
                            PlayListScreen(
                                playListViewModel = playListViewModel,
                                tracksViewModel = audioViewModel,
                                navController
                            )
                        }


                    }
                }

            }
        }


        composable(
            Screens.TrackScreen.name
        )
        {
            val currentTrackList = audioViewModel.currentTrackList.collectAsState()
            TrackScreen(
                tracksListViewModel = audioViewModel,
                mediaPlayer = mediaPlayer,
                navController = navController,
                audioList = currentTrackList.value,
                tracksViewModel = audioViewModel
            )
        }

        composable(
            route = "${Screens.AudiosTrackScreen.name}/{PID}",
            arguments = listOf(navArgument("PID") { type = NavType.StringType })
        ) { nav ->
            var update by rememberSaveable {
                mutableStateOf(true)
            }
            val playListID = nav.arguments?.getString("PID")!!
            val pl = remember {
                mutableStateOf<List<AudioTrackEntity>>(emptyList())
            }
            LaunchedEffect(key1 = update) {
                pl.value = playListViewModel.getPlayList(playListID)
            }
            Scaffold(topBar = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "")
                }
            }, bottomBar = {
                if (currentTrack.value.uri != Uri.EMPTY) {
                    AudioBottomBar(
                        mediaPlayer,
                        audioViewModel,
                    )
                }
            }) {
                Column(Modifier.padding(it)) {
                    IconButton(onClick = {

                        if (audioViewModel.currentTrackList.value
                            != pl.value
                        ) {
                            audioViewModel.newCurrentTrackList(pl.value)
                        }
                        Intent(
                            localContext.applicationContext,
                            AudioForeGroundService::class.java
                        ).also {
                            it.action = Actions.Shuffle.name
                            localContext.startService(it)
                        }
                    }, modifier = Modifier.align(Alignment.End)) {
                        Icon(imageVector = Icons.Filled.Shuffle, contentDescription = "")
                    }
                    AudiosScreen<Audio>(
                        audioList = pl.value,
                        mediaPlayer,
                        tracksViewModel = audioViewModel,
                        playListViewModel,
                        dropDownActions = listOf(
                            DropDownActions("remove") {
                                playListViewModel.removeFromPLayList(
                                    AudioTrack(
                                        it.name, it.id, it.uri, it.length
                                    ), UUID.fromString(playListID)
                                )
                                Toast.makeText(localContext, "deleted", Toast.LENGTH_SHORT)
                                    .show()
                                update = !update
                            },
                            DropDownActions("Share") {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    setDataAndType(it.uri, "audio/*")
                                }
                                if (intent.resolveActivity(localContext.packageManager) != null) {
                                    localContext.startActivity(intent)
                                }
                            })
                    )
                }
            }
        }
    }
}