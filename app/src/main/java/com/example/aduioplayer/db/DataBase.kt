package com.example.aduioplayer.db

import android.net.Uri
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.aduioplayer.ui.theme.AudioTrackEntity
import com.example.aduioplayer.ui.theme.PlayList

@Database(version = 4, entities = [AudioTrackEntity::class, PlayList::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDataBase() : RoomDatabase() {
    abstract fun dao(): PlayListDao
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String): Uri {
        return Uri.parse(value)
    }

    @TypeConverter
    fun dateToTimestamp(uri: Uri): String {
        return uri.toString()
    }
}