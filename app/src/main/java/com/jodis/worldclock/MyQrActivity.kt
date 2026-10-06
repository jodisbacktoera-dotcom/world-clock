package com.jodis.worldclock

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream

class MyQrActivity : AppCompatActivity() {

    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_qr)

        findViewById<TextView>(R.id.backBtn).setOnClickListener { finish() }

        val username = UserData.getUsername(this)
        val displayName = UserData.getDisplayName(this)
        val phone = UserData.getPhone(this)

        if (username.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Setup complete nahi hai", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val qrData = QrGenerator.buildQrData(username, displayName, phone)
        qrBitmap = QrGenerator.generateQr(qrData)

        val qrImage = findViewById<ImageView>(R.id.qrImage)
        qrImage.setImageBitmap(qrBitmap)

        findViewById<TextView>(R.id.qrUsername).text = "@$username"
        findViewById<TextView>(R.id.qrDisplayName).text = displayName

        findViewById<Button>(R.id.btnSaveQr).setOnClickListener {
            saveQrToGallery()
        }

        findViewById<Button>(R.id.btnShareQr).setOnClickListener {
            shareQr()
        }
    }

    private fun saveQrToGallery() {
        val bitmap = qrBitmap ?: return
        try {
            val filename = "WorldClock_QR_${System.currentTimeMillis()}.png"
            var uri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val outputStream: OutputStream? = uri?.let { contentResolver.openOutputStream(it) }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream?.close()
            } else {
                val path = MediaStore.Images.Media.insertImage(
                    contentResolver, bitmap, filename, "World Clock QR"
                )
                uri = Uri.parse(path)
            }

            Toast.makeText(this, "QR Gallery me save ho gaya ✅", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Save nahi hua: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareQr() {
        val bitmap = qrBitmap ?: return
        try {
            val path = MediaStore.Images.Media.insertImage(
                contentResolver, bitmap, "WorldClock_QR", "Share QR"
            )
            val uri = Uri.parse(path)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            startActivity(Intent.createChooser(shareIntent, "Share QR"))
        } catch (e: Exception) {
            Toast.makeText(this, "Share nahi hua", Toast.LENGTH_SHORT).show()
        }
    }
}
