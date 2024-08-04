package com.example.aduioplayer.frservice

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.io.IOException

sealed class AudioActions(uri: Uri = Uri.EMPTY, mediaPlayer: MediaPlayer) {

    class Start(private val context: Context, val uri: Uri, val mediaPlayer: MediaPlayer) :
        AudioActions(uri, mediaPlayer = mediaPlayer) {
        fun start() {
            Log.d("MediaPlayer","Starting")
                mediaPlayer.reset()
            try {
                mediaPlayer.apply {
                    setOnPreparedListener {
                        start()
                    }
                    setOnErrorListener { mediaPlayer, what, extra ->
                        Log.d("MediaPlayer error accourd", "$what,$extra")
                        true
                    }
                    setDataSource(context, uri)
                    prepareAsync()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("MediaPlayer", "IOException: ${e.message}")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                Log.e("MediaPlayer", "IllegalArgumentException: ${e.message}")
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e("MediaPlayer", "SecurityException: ${e.message}")
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                Log.e("MediaPlayer", "IllegalStateException: ${e.message}")
            }


        }
    }

}