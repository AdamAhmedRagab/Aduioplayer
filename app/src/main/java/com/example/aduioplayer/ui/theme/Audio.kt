package com.example.aduioplayer.ui.theme

import android.net.Uri

import java.util.UUID

interface Audio {
    val uri: Uri
    val name:String
    val id:UUID
    val length: Long
}