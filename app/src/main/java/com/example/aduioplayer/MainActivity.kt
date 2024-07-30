package com.example.aduioplayer

import android.Manifest
import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.PermissionRequest
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aduioplayer.navigation.Nav
import com.example.aduioplayer.ui.theme.AduioplayerTheme
import com.example.aduioplayer.ui.theme.AudioBottomBar
import com.example.aduioplayer.ui.theme.AudioTrack
import com.example.aduioplayer.ui.theme.AudiosScreen
import com.example.aduioplayer.ui.theme.DropDownActions
import com.example.aduioplayer.ui.theme.PlayListScreen
import com.example.aduioplayer.ui.theme.PlayListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                // Permission is granted
                println("$permissionName granted")
            } else {
                // Permission is denied
                println("$permissionName denied")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AduioplayerTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.FOREGROUND_SERVICE,
                            Manifest.permission.READ_MEDIA_AUDIO,
                        ),
                        0
                    )
                }
                else{
                    val requiredPermissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.FOREGROUND_SERVICE,
                    )
                    permissionsLauncher.launch(requiredPermissions)

                }
                val audioViewModel = viewModel<TracksViewModel>()
                val playListViewModel = viewModel<PlayListViewModel>()
                LaunchedEffect(key1 = true) {
                val projection =
                    arrayOf(
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                    )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    contentResolver.query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                    )?.use {
                        val idIndex = it.getColumnIndex(projection[0])
                        val nameIndex = it.getColumnIndex(projection[1])
                        val lengthIndex = it.getColumnIndex(projection[2])
                        val audios = mutableListOf<AudioTrack>()
                        while (it.moveToNext()) {
                            val id = it.getLong(idIndex)
                            val name = it.getString(nameIndex)
                            val uri =
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    id,
                                )
                            val length = it.getLong(lengthIndex)
                            audios.add(AudioTrack(name, uri = uri, length = length))
                        }
                        audioViewModel.addingTracks(audios)
                    }
                }}
                Nav()


            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AduioplayerTheme {

    }
}
