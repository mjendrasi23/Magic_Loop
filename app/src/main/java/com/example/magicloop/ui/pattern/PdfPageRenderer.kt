package com.example.magicloop.ui.pattern

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File

object PdfPageRenderer {

    fun renderPage(pdfPath: String, pageIndex: Int, targetWidthPx: Int): Bitmap? {
        val file = File(pdfPath)
        if (!file.exists()) return null

        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                if (pageIndex !in 0 until renderer.pageCount) return null

                renderer.openPage(pageIndex).use { page ->
                    val scale = targetWidthPx.toFloat() / page.width
                    val targetHeightPx = (page.height * scale).toInt().coerceAtLeast(1)

                    val bitmap = Bitmap.createBitmap(
                        targetWidthPx, targetHeightPx, Bitmap.Config.ARGB_8888
                    )
                    bitmap.eraseColor(android.graphics.Color.WHITE)

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    return bitmap
                }
            }
        }
    }
}