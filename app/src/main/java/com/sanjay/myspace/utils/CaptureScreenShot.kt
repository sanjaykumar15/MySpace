package com.sanjay.myspace.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.sanjay.myspace.utils.DateFormatConstants.fileDateFormat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ShareScreenShot(
    private val context: Context,
) {

    fun share(bitmap: Bitmap) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, getUriFromBitmap(bitmap))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    private fun getUriFromBitmap(bitmap: Bitmap): Uri? {
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val folder = (context.externalCacheDir ?: context.cacheDir).path + "/screenShots/"
            val directory = File(folder)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(folder, "${fileDateFormat.format(System.currentTimeMillis())}.jpg")
            try {
                file.createNewFile()
                val fo = FileOutputStream(file)
                fo.write(bytes.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        } catch (e: Exception) {
            Log.e("CaptureScreenShot", "getUriFromBitmap", e)
        }
        return null
    }

}