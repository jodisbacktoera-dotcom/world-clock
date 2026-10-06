package com.jodis.worldclock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddFriendActivity : AppCompatActivity() {

    private val PICK_IMAGE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        findViewById<TextView>(R.id.backBtn).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data ?: return

            val statusText = findViewById<TextView>(R.id.statusText)
            statusText.text = "Scan kar raha hoon..."

            val qrData = QrScanner.scanFromUri(this, uri)

            if (qrData == null) {
                statusText.text = "QR nahi mila. Dobara try karo."
                Toast.makeText(this, "QR scan nahi hua", Toast.LENGTH_SHORT).show()
                return
            }

            val parsed = QrGenerator.parseQrData(qrData)
            if (parsed == null) {
                statusText.text = "QR World Clock ka nahi hai."
                Toast.makeText(this, "Galat QR", Toast.LENGTH_SHORT).show()
                return
            }

            val (username, displayName, phone) = parsed

            // Khud ka QR scan nahi karna
            if (username == UserData.getUsername(this)) {
                statusText.text = "Yeh aapka khud ka QR hai."
                Toast.makeText(this, "Apna QR scan nahi kar sakte", Toast.LENGTH_SHORT).show()
                return
            }

            // Friend save karo
            FriendStore.saveFriend(this, username, displayName, phone)

            statusText.text = "✅ $displayName add ho gaya!"
            Toast.makeText(this, "$displayName add ho gaya!", Toast.LENGTH_SHORT).show()

            // 1.5 sec baad finish
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                finish()
            }, 1500)
        }
    }
}
