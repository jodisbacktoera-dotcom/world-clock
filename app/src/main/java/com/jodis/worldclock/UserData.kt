package com.jodis.worldclock

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

object UserData {

    private const val PREFS = "world_clock_user"
    private const val KEY_USERNAME = "username"
    private const val KEY_DISPLAY_NAME = "display_name"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_PHONE = "phone"
    private const val KEY_SETUP_DONE = "setup_done"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    // Setup complete hua ya nahi
    fun isSetupDone(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_SETUP_DONE, false)
    }

    // Save user data
    fun saveUser(
        context: Context,
        username: String,
        displayName: String,
        pin: String,
        phone: String
    ) {
        prefs(context).edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putString(KEY_PIN_HASH, hashPin(pin))
            .putString(KEY_PHONE, phone)
            .putBoolean(KEY_SETUP_DONE, true)
            .apply()
    }

    // PIN check karo
    fun verifyPin(context: Context, pin: String): Boolean {
        val savedHash = prefs(context).getString(KEY_PIN_HASH, "") ?: return false
        return savedHash == hashPin(pin)
    }

    // Getters
    fun getUsername(context: Context): String =
        prefs(context).getString(KEY_USERNAME, "") ?: ""

    fun getDisplayName(context: Context): String =
        prefs(context).getString(KEY_DISPLAY_NAME, "") ?: ""

    fun getPhone(context: Context): String =
        prefs(context).getString(KEY_PHONE, "") ?: ""

    // SHA-256 hash
    private fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
