package com.example.aduioplayer

import android.Manifest
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aduioplayer.applicationLayer.TracksViewModel
import com.example.aduioplayer.frservice.AudioForeGroundService
import com.example.aduioplayer.ui.theme.AduioplayerTheme
import com.example.aduioplayer.ui.theme.AudioTrack
import com.example.aduioplayer.uilayer.navigation.Nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var mBound by mutableStateOf(false)
    private lateinit var mService: AudioForeGroundService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d("BINDER CLASS", "Getting Media Player")
            val binder = service as AudioForeGroundService.MediaPlayerBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                println("$permissionName granted")
            } else {
                println("$permissionName denied")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
            } else {
                val requiredPermissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.FOREGROUND_SERVICE,
                )
                permissionsLauncher.launch(requiredPermissions)

            }
            val audioViewModel = viewModel<TracksViewModel>()
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
                }
            }
            var isServiceBound by rememberSaveable(mBound) {
                mutableStateOf(mBound)
            }
            DisposableEffect(Unit) {
                Intent(this@MainActivity, AudioForeGroundService::class.java).also {
                    bindService(it, connection, Context.BIND_AUTO_CREATE)
                }

                onDispose {
                    if (mBound) {
                        mBound = false
                    }
                }
            }
            LaunchedEffect(key1 = mBound) {
                isServiceBound = mBound
            }
            AduioplayerTheme {
                if (isServiceBound) {
                    Nav(getMediaPlayer())
                }
            }
        }
    }

    private fun getMediaPlayer(): MediaPlayer {
        return mService.mediaPlayer
    }

}


@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AduioplayerTheme {

    }
}
