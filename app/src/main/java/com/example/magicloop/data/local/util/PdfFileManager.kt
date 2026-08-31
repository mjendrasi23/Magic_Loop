package com.example.magicloop.data.local.util

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.graphics.pdf.PdfRenderer
import java.io.File

object PdfFileManager {

    private fun patternDir(context: Context): File =
        File(context.filesDir, "patterns").apply { mkdirs() }

    fun importPdf(context: Context, sourceUri: Uri, projectId: Long): String {
        val destFile = File(patternDir(context), "project_${projectId}_pattern.pdf")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Nije moguće otvoriti odabranu datoteku")
        return destFile.absolutePath
    }

    fun getPageCount(pdfPath: String): Int {
        val file = File(pdfPath)
        if (!file.exists()) return 0
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                return renderer.pageCount
            }
        }
    }

    fun deletePdf(pdfPath: String) {
        File(pdfPath).takeIf { it.exists() }?.delete()
    }
}