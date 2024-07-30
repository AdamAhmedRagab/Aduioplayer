package com.example.aduioplayer.ui.theme

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aduioplayer.db.PlayListDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(private val playListDao: PlayListDao) : ViewModel() {
    private val _playLists: MutableStateFlow<List<PlayList>> = MutableStateFlow(emptyList())
    val playLists = _playLists.asStateFlow()
    val audios = mutableSetOf<List<AudioTrackEntity>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playListDao.getPlayLists().conflate().distinctUntilChanged()
                .collect() {
                    if (it.isEmpty()) {
                        Log.d("PlayListCount", "0")
                    } else {
                        _playLists.value = it
                        Log.d("playlist count", _playLists.value.size.toString())
                    }
                }
        }
    }

    fun createPlayList(playList: PlayList) =
        viewModelScope.launch(Dispatchers.IO) { playListDao.addPlayList(playList);getAllPlaylists() }

    fun deletePlayList(playList: PlayList) =
        viewModelScope.launch(Dispatchers.IO) { playListDao.deletePlayList(playList);getAllPlaylists() }

    fun removeFromPLayList(audioTrackEntity: AudioTrack, playListID: UUID) =
        viewModelScope.launch(Dispatchers.IO) {
            playListDao.removeAudio(
                AudioTrackEntity(
                    audioTrackEntity.name,
                    id = audioTrackEntity.id,
                    uri = audioTrackEntity.uri,
                    length = audioTrackEntity.length,
                    playListId = playListID
                )
            )
        }

    fun addToPLayList(audioTrackEntity: AudioTrack, playListID: UUID) =
        viewModelScope.launch(Dispatchers.IO) {
            playListDao.addAudio(
                AudioTrackEntity(
                    audioTrackEntity.name,
                    uri = audioTrackEntity.uri,
                    length = audioTrackEntity.length,
                    playListId = playListID
                )
            )
        }

    private suspend fun getAllPlaylists() {
        playListDao.getPlayLists().flowOn(Dispatchers.IO).conflate().distinctUntilChanged()
            .collect {
                if (it.isEmpty()) {
                    Log.d("PlayListCount", "0")
                } else {
                    _playLists.value = it
                }

            }
    }
        suspend fun getPlayList(id: String): List<AudioTrackEntity> {
            val cachedPl = audios.find { it.first().id.toString() == id }
            lateinit var pl: List<AudioTrackEntity>
            return if (cachedPl.isNullOrEmpty()) {
             withContext(Dispatchers.IO) {
                    playListDao.getAudios(UUID.fromString(id))
               }

            } else {
                 cachedPl
            }
        }

}


