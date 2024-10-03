package com.movtery.sodiumautofix.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.movtery.sodiumautofix.R
import org.apache.commons.io.FileUtils
import java.io.File

class FileTools {
    companion object {
        fun copyFile(context: Context, fileUri: Uri, rootPath: String): File {
            val fileName = getFileName(context, fileUri)
            val outputFile = File(rootPath, fileName)
            runCatching {
                context.contentResolver.openInputStream(fileUri).use { inputStream ->
                    FileUtils.copyInputStreamToFile(inputStream, outputFile)
                }
            }.getOrElse { e ->
                throw RuntimeException(e)
            }

            return outputFile
        }

        fun shareFile(activity: Activity, file: File) {
            val contentUri = FileProvider.getUriForFile(activity,"com.movtery.sodiumautofix.fileprovider", file)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.setDataAndType(contentUri, "*/*")

            val sendIntent = Intent.createChooser(shareIntent, file.name)
            activity.startActivity(sendIntent)
        }

        fun getFileName(ctx: Context, uri: Uri): String {
            val c = ctx.contentResolver.query(uri, null, null, null, null) ?: return uri.lastPathSegment!!

            c.moveToFirst()
            val columnIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex == -1) return uri.lastPathSegment!!
            val fileName = c.getString(columnIndex)
            c.close()
            return fileName
        }
    }
}