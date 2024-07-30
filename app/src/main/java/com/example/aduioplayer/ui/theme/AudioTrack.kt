package com.example.aduioplayer.ui.theme

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Stable
data class AudioTrack(
    override val name: String,
    override val id: UUID = UUID.randomUUID(),
    override val uri: Uri,
    override val length: Long
):Audio

@Entity(tableName = "AudiosTabel")
@Stable
data class AudioTrackEntity(
    override val name: String,
    @PrimaryKey()
    @ColumnInfo(name="id") override val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name="uri") override val uri: Uri,
    @ColumnInfo(name="length") override val length: Long,
    val playListId :UUID
):Audio

    @Entity(tableName = "playLists")
    @Stable
    data class PlayList(val name: String, @PrimaryKey val id: UUID= UUID.randomUUID())