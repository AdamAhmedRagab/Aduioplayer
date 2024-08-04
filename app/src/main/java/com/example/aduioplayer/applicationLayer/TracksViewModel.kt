package com.example.aduioplayer.applicationLayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aduioplayer.db.dataRepo
import com.example.aduioplayer.ui.theme.Audio
import com.example.aduioplayer.ui.theme.AudioTrack
import com.example.aduioplayer.ui.theme.PlayList

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


