package com.example.aduioplayer.db

import android.net.Uri
import android.util.Log
import com.example.aduioplayer.ui.theme.Audio
import com.example.aduioplayer.ui.theme.AudioTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object DataRepo {

    private val _currentTrack = MutableStateFlow(
        AudioTrack(
            "",
            uri = Uri.EMPTY,
            length = 0
        )
    )

    val currentTrack get() = _currentTrack
    private val _currentTrackList = MutableStateFlow<List<Audio>>(emptyList())
    val currentTrackList get()= _currentTrackList
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying get() = _isPlaying
    fun newCurrentTrack(newData: AudioTrack) {
        _currentTrack.update { newData }
        Log.d("MediaPlayer","this audio will be played soon:${_currentTrack.value.uri}")
    }

    fun isPlaying(value: Boolean) {
        _isPlaying.update { value }
    }

    fun newCurrentTrackList(newData: List<Audio>) {
        _currentTrackList.value=newData
        Log.d("MediaPlayer","this  is the current track list:$newData")
}}

val dataRepo = DataRepo
