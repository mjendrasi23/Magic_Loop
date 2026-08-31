package com.example.magicloop.data.local.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageFileManager {

    private fun imagesDir(context: Context): File =
        File(context.filesDir, "project_images").apply { mkdirs() }

    fun importImage(context: Context, sourceUri: Uri, projectId: Long): String {
        val fileName = "project_${projectId}_${UUID.randomUUID()}.jpg"
        val destFile = File(imagesDir(context), fileName)
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Nije moguće otvoriti odabranu sliku")
        return destFile.absolutePath
    }

    fun deleteImage(imagePath: String) {
        File(imagePath).takeIf { it.exists() }?.delete()
    }
}