package com.example.onlinestore.presentation.ui

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** Создаёт пустой файл для фото во внутреннем хранилище приложения (files/photos). */
fun createImageFile(context: Context): File {
    val dir = File(context.filesDir, "photos").apply { mkdirs() }
    return File(dir, "product_${System.currentTimeMillis()}.jpg")
}

/** content:// URI через FileProvider — именно его нужно отдавать приложению «Камера». */
fun fileToContentUri(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
