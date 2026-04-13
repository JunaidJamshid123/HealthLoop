package com.example.healthloop.data.local.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    
    private const val MAX_IMAGE_SIZE = 500 // Max width/height in pixels
    private const val COMPRESSION_QUALITY = 80 // JPEG quality (0-100)
    private const val PROFILE_IMAGE_DIR = "profile_images"
    private const val PROFILE_IMAGE_NAME = "profile_picture.jpg"

    /**
     * Saves image from URI to internal storage and returns the file path.
     */
    fun saveImageToFile(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val originalBitmap = BitmapFactory.decodeStream(stream)
                val rotatedBitmap = rotateImageIfRequired(context, uri, originalBitmap)
                val resizedBitmap = resizeBitmap(rotatedBitmap, MAX_IMAGE_SIZE)
                saveBitmapToFile(context, resizedBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves a bitmap to internal storage and returns the absolute file path.
     */
    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): String {
        val dir = File(context.filesDir, PROFILE_IMAGE_DIR)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, PROFILE_IMAGE_NAME)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, fos)
        }
        return file.absolutePath
    }

    /**
     * Loads a bitmap from a file path.
     */
    fun loadBitmapFromFile(filePath: String): Bitmap? {
        return try {
            val file = File(filePath)
            if (file.exists()) BitmapFactory.decodeFile(filePath) else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes the profile image file.
     */
    fun deleteProfileImage(context: Context) {
        try {
            val file = File(context.filesDir, "$PROFILE_IMAGE_DIR/$PROFILE_IMAGE_NAME")
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Resolves a stored value to a Bitmap.
     * Supports both legacy Base64 strings and new file paths.
     */
    fun resolveToBitmap(storedValue: String): Bitmap? {
        // If it looks like a file path, load from file
        if (storedValue.startsWith("/") || storedValue.contains("/profile_images/")) {
            return loadBitmapFromFile(storedValue)
        }
        // Otherwise treat as legacy Base64
        return base64ToBitmap(storedValue)
    }
    
    /**
     * Converts a URI to a Base64 encoded string (legacy — kept for export compatibility)
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val originalBitmap = BitmapFactory.decodeStream(stream)
                val rotatedBitmap = rotateImageIfRequired(context, uri, originalBitmap)
                val resizedBitmap = resizeBitmap(rotatedBitmap, MAX_IMAGE_SIZE)
                bitmapToBase64(resizedBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Converts a Bitmap to Base64 string
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    
    /**
     * Converts a Base64 string back to a Bitmap
     */
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Resizes a bitmap maintaining aspect ratio
     */
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        
        val ratio = width.toFloat() / height.toFloat()
        
        val newWidth: Int
        val newHeight: Int
        
        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Rotates the image if required based on EXIF data
     */
    private fun rotateImageIfRequired(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                
                val rotationDegrees = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
                
                if (rotationDegrees != 0f) {
                    val matrix = Matrix()
                    matrix.postRotate(rotationDegrees)
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }
            } ?: bitmap
        } catch (e: Exception) {
            bitmap
        }
    }
    
    /**
     * Gets the size of the Base64 string in KB
     */
    fun getBase64SizeInKB(base64String: String): Double {
        val bytes = Base64.decode(base64String, Base64.DEFAULT)
        return bytes.size / 1024.0
    }
}
