package com.example.sosapp.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.convertBitMapToBase64String(): String {
    val outStream = ByteArrayOutputStream()
    this.compress(
        Bitmap.CompressFormat.JPEG, 100, outStream
    )
    val byteArray = outStream.toByteArray()
    val imageBase64String = Base64.encodeToString(
        byteArray, Base64.DEFAULT
    )
    return imageBase64String
}