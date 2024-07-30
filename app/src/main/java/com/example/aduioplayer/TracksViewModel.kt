package com.example.aduioplayer

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aduioplayer.ui.theme.Audio
import com.example.aduioplayer.ui.theme.AudioTrack
import com.example.aduioplayer.ui.theme.PlayList
import com.example.aduioplayer.ui.theme.dataRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class TracksViewModel : ViewModel() {
    var newPlayList by mutableStateOf(PlayList(""))
    var tracksList by mutableStateOf(emptyList<AudioTrack>())
        private set
    val currentTrackList get()= dataRepo.currentTrackList
    val isPlaying get() = dataRepo.isPlaying
    val currentTrack = dataRepo.currentTrack
    fun addingTracks(tracksList: List<AudioTrack>) {
        this.tracksList = tracksList
    }
    fun newCurrentTrack(audio: AudioTrack){
        dataRepo.newCurrentTrack(audio)
    }
    fun newCurrentTrackList(audio: List<Audio>){
        dataRepo.newCurrentTrackList(audio)
    }
    }


