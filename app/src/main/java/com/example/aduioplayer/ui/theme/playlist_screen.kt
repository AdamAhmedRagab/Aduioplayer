package com.example.aduioplayer.ui.theme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aduioplayer.TracksViewModel
import com.example.aduioplayer.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListScreen(
    playListViewModel: PlayListViewModel,
    tracksViewModel: TracksViewModel,
    navController: NavController
) {

    var playListName by rememberSaveable {
        mutableStateOf("")
    }

    var isCreatingPlayList by rememberSaveable {
        mutableStateOf(false)
    }
    Column {
        Button(onClick = { isCreatingPlayList = true }) {
            Text(text = "New PlayList")
        }
        if (isCreatingPlayList) {
            BasicAlertDialog(onDismissRequest = { isCreatingPlayList = false }) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = playListName,
                        onValueChange = { playListName = it },
                        placeholder = {
                            Text(
                                text = "PlayList name"
                            )
                        })
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Right
                    ) {
                        Button(onClick = {
                            tracksViewModel.newPlayList = tracksViewModel.newPlayList.copy(
                                name = playListName,
                            )
                            playListViewModel.createPlayList(tracksViewModel.newPlayList)
                        }) {
                            Text(text = "OK")
                        }

                    }

                }
            }
        }
        LazyColumn {
            items(items = playListViewModel.playLists.value, key = { it.id }) {
                Box {
                    var isOpen by rememberSaveable {

                        mutableStateOf(false)
                    }
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 10.dp,
                                vertical = 10.dp
                            )
                            .height(70.dp)
                            .clickable { navController.navigate(Screens.AudiosTrackScreen.name + "/${it.id}") }) {
                        Row {
                        Text(text = it.name, Modifier.padding(horizontal = 10.dp))
                            IconButton(
                                onClick = { isOpen = true },
                                Modifier.wrapContentWidth(Alignment.End)
                            ) {
                                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "")
                        }
                    }}
                    DropdownMenu(expanded =isOpen, onDismissRequest = { isOpen=false }) {
                        DropdownMenuItem(text = { Text(text ="Delete")}, onClick = { playListViewModel.deletePlayList(it)})
                    }

                }
            }
        }
    }
}