package com.lookie.socialdownloader.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.lookie.socialdownloader.R
import okhttp3.ResponseBody
import java.io.*

object FileUtils {

  fun scanFile(context: Context?, file: File) {
    context!!.sendBroadcast(
      Intent(
        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
        Uri.fromFile(file)
      )
    )
  }

  @Throws(IOException::class)
  fun createImageFile(ctx: Context, imageFileName: String): File {
    val storageDir = File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
      ctx.getString(R.string.app_name)
    )
    if (!storageDir.exists()) {
      storageDir.mkdirs()
    }
    return File(storageDir, imageFileName)
  }

  fun writeResponseBodyToDisk(
    body: ResponseBody,
    file: File
  ): Boolean {
    return try {
      var inputStream: InputStream? = null
      var outputStream: OutputStream? = null

      try {
        val fileReader = ByteArray(4096)
        val fileSize = body.contentLength()
        var fileSizeDownloaded: Long = 0

        inputStream = body.byteStream()
        outputStream = FileOutputStream(file)

        while (true) {
          val read = inputStream.read(fileReader)
          if (read == -1) {
            break
          }
          outputStream.write(fileReader, 0, read)
          fileSizeDownloaded += read.toLong()
        }
        outputStream.flush()
        true
      } catch (e: IOException) {
        e.printStackTrace()
        false
      } finally {
        inputStream?.close()
        outputStream?.close()
      }
    } catch (e: IOException) {
      e.printStackTrace()
      false
    }
  }
}