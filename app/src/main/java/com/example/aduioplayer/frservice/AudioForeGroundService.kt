package com.example.aduioplayer.frservice

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.aduioplayer.db.dataRepo
import com.example.aduioplayer.ui.theme.AudioTrack

class AudioForeGroundService : Service() {
    val mediaPlayer = MediaPlayer()
    override fun onBind(p0: Intent?): IBinder {
        return mediaPlayerBinder
    }

    private var action: String? = null
    private val mediaPlayerBinder = MediaPlayerBinder()
    val currentTrack = dataRepo.currentTrack
    val currentTrackList = dataRepo.currentTrackList

    inner class MediaPlayerBinder : Binder() {
        fun getService(): AudioForeGroundService = this@AudioForeGroundService
    }

    private fun shufflePlay() {
        mediaPlayer.reset()
        val ss = currentTrackList.value.random()
        mediaPlayer.setDataSource(
            applicationContext,
            ss.uri
        )
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            dataRepo.newCurrentTrack(
                AudioTrack(
                    ss.name,
                    ss.id,
                    ss.uri,
                    ss.length
                )
            )
            mediaPlayer.start()
        }
        mediaPlayer.setOnCompletionListener {
            shufflePlay()
        }
    }

    private fun playList(index: Int) {
        val trackkWillBePlayed = dataRepo.currentTrackList.value.getOrNull(index)
        trackkWillBePlayed?.let {
            dataRepo.isPlaying(true)
            dataRepo.newCurrentTrack(
                AudioTrack(
                    trackkWillBePlayed.name,
                    trackkWillBePlayed.id,
                    trackkWillBePlayed.uri,
                    trackkWillBePlayed.length
                )
            )
            AudioActions.Start(applicationContext, trackkWillBePlayed.uri, mediaPlayer)
                .start()
            mediaPlayer.setOnCompletionListener {
                playList(index + 1)

            }
        }

    }

    private fun autoNext() {
        val trackkWillBePlayed =
            if (currentTrackList.value.last().id != currentTrack.value.id) currentTrackList.value[currentTrackList.value.indexOfFirst { it.id == currentTrack.value.id }
                    + 1]
            else currentTrackList.value.first()
        dataRepo.isPlaying(true)
        dataRepo.newCurrentTrack(
            AudioTrack(
                trackkWillBePlayed.name,
                trackkWillBePlayed.id,
                trackkWillBePlayed.uri,
                trackkWillBePlayed.length
            )
        )
        AudioActions.Start(applicationContext, trackkWillBePlayed.uri, mediaPlayer)
            .start()
        mediaPlayer.setOnCompletionListener { autoNext() }
    }

    private fun audioLoop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.apply {
                setOnCompletionListener {
                    AudioActions.Start(
                        applicationContext,
                        currentTrack.value.uri,
                        mediaPlayer
                    )
                        .start()
                    mediaPlayer.setOnCompletionListener {
                        audioLoop()
                    }
                }
            }
        } else {
            AudioActions.Start(
                applicationContext,
                currentTrack.value.uri,
                mediaPlayer
            )
                .start()
            mediaPlayer.setOnCompletionListener {
                audioLoop()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("audio service", "starting")

        when (intent?.action) {
            Actions.Select.name -> {
                Log.d("Foreground service", "current track $currentTrack")
                if (currentTrack.value.uri != Uri.EMPTY) {
                    AudioActions.Start(
                        applicationContext,
                        currentTrack.value.uri, mediaPlayer
                    ).start()
                    dataRepo.isPlaying(true)
                } else {
                    Log.d("MediaPlayer", "uri is empty = ${currentTrack.value.uri == Uri.EMPTY}")
                }
            }

            Actions.NEXT.name -> {
                Log.d("MediaPlayer", "next audio will be played")
                if (action != Actions.Shuffle.name) {
                    val trackkWillBePlayed =
                        if (currentTrackList.value.last().id != currentTrack.value.id) currentTrackList.value.getOrNull(
                            currentTrackList.value.indexOfFirst { it.id == currentTrack.value.id }
                                    + 1)
                        else currentTrackList.value.first()
                    trackkWillBePlayed?.let {
                        dataRepo.isPlaying(true)
                        dataRepo.newCurrentTrack(
                            AudioTrack(
                                trackkWillBePlayed.name,
                                trackkWillBePlayed.id,
                                trackkWillBePlayed.uri,
                                trackkWillBePlayed.length
                            )
                        )
                        AudioActions.Start(applicationContext, trackkWillBePlayed.uri, mediaPlayer)
                            .start()
                    }
                } else {
                    when (action) {
                        Actions.Shuffle.name -> {
                            shufflePlay()
                        }

                        Actions.AutoNext.name -> {
                            autoNext()
                        }

                        Actions.PlayList.name -> {
                            autoNext()
                        }

                        Actions.Loop.name -> {
                            audioLoop()
                        }
                    }
                }
            }


            Actions.Pause.name -> {
                mediaPlayer.pause();dataRepo.isPlaying(false)
            }

            Actions.Play.name -> {
                mediaPlayer.start()
                dataRepo.isPlaying(true)
                mediaPlayer.setOnCompletionListener {
                    when (action) {
                        Actions.Shuffle.name -> {
                            shufflePlay()
                        }

                        Actions.AutoNext.name -> {
                            autoNext()
                        }

                        Actions.PlayList.name -> {
                            autoNext()
                        }

                        Actions.Loop.name -> {
                            audioLoop()
                        }
                    }
                }
            }

            Actions.PREV.name -> {
                if (currentTrackList.value.isNotEmpty()) {
                    val trackkWillBePlayed =
                        if (currentTrackList.value.first().id != currentTrack.value.id) currentTrackList.value[currentTrackList.value.indexOfFirst { it.id == currentTrack.value.id }
                                - 1]
                        else currentTrackList.value.last()

                    dataRepo.isPlaying(true)
                    AudioActions.Start(applicationContext, trackkWillBePlayed.uri, mediaPlayer)
                        .start()
                    dataRepo.newCurrentTrack(
                        AudioTrack(
                            trackkWillBePlayed.name,
                            trackkWillBePlayed.id,
                            trackkWillBePlayed.uri,
                            trackkWillBePlayed.length
                        )
                    )
                    mediaPlayer.setOnCompletionListener {
                        when (action) {
                            Actions.Shuffle.name -> {
                                shufflePlay()
                            }

                            Actions.AutoNext.name -> {
                                autoNext()
                            }

                            Actions.PlayList.name -> {
                                autoNext()
                            }

                            Actions.Loop.name -> {
                                audioLoop()
                            }
                        }
                    }

                }
            }

            Actions.Shuffle.name -> {
                action = Actions.Shuffle.name


                shufflePlay()
            }

            Actions.Cancel.name -> {
                dataRepo.isPlaying(false);dataRepo.newCurrentTrack(
                    AudioTrack("", uri = Uri.EMPTY, length = 0)
                );mediaPlayer.reset();stopSelf()
            }

            Actions.PlayList.name -> {
                action = Actions.PlayList.name

                playList(0)
            }

            Actions.Loop.name -> {
                action = Actions.Loop.name

                audioLoop()
            }

            Actions.AutoNext.name -> {
                action = Actions.AutoNext.name
                if (currentTrackList.value.isNotEmpty()) {
                    autoNext()
                } else {
                    Toast.makeText(applicationContext, "the playlist is empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        val notifaction = NotificationCompat.Builder(this, "Audio_controller")
            .setContentTitle(currentTrack.value.name)
            .setContentText(currentTrack.value.length.toString()).build()
        if (intent?.action !in arrayOf(
                Actions.PREV.name,
                Actions.NEXT.name,
                Actions.Play.name,
                Actions.Pause.name,
            )
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.startForeground(1, notifaction, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                this.startForeground(1, notifaction)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this).notify(1, notifaction)
            }
        }


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}

enum class Actions {
    Play,
    Pause,
    NEXT,
    PREV,
    Select,
    Shuffle,
    Cancel,
    PlayList,
    Loop,
    AutoNext
}