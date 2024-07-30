package com.example.aduioplayer.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.aduioplayer.ui.theme.AudioTrack
import com.example.aduioplayer.ui.theme.AudioTrackEntity
import com.example.aduioplayer.ui.theme.PlayList
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PlayListDao {
    @Insert
    suspend fun addAudio(audioTrack: AudioTrackEntity)

    @Delete
    suspend fun removeAudio(audioTrack: AudioTrackEntity)

    @Insert
    suspend fun addPlayList(playList: PlayList)

    @Delete
    suspend fun deletePlayList(playList: PlayList)

    @Query("SELECT * FROM PLAYLISTS")
    fun getPlayLists(): Flow<List<PlayList>>

    @Query("SELECT * FROM AudiosTabel WHERE playListId = :id")
    fun getAudios(id: UUID): List<AudioTrackEntity>
}