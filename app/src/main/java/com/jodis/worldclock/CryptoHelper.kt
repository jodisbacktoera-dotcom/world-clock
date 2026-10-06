package com.jodis.worldclock

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    private const val SECRET = "WorldClockApp2024SecretKey1234"
    private const val ALGO = "AES"

    private fun getKey(): SecretKeySpec {
        val key = SECRET.padEnd(32, '0').take(32).toByteArray()
        return SecretKeySpec(key, ALGO)
    }

    fun encrypt(plainText: String): String {
        return try {
            val cipher = Cipher.getInstance(ALGO)
            cipher.init(Cipher.ENCRYPT_MODE, getKey())
            val encrypted = cipher.doFinal(plainText.toByteArray())
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            ""
        }
    }

    fun decrypt(encryptedText: String): String {
        return try {
            val cipher = Cipher.getInstance(ALGO)
            cipher.init(Cipher.DECRYPT_MODE, getKey())
            val decoded = Base64.decode(encryptedText, Base64.NO_WRAP)
            String(cipher.doFinal(decoded))
        } catch (e: Exception) {
            ""
        }
    }
}
