package com.example.wetherapp.controler.controler

import android.content.Context

class AppSettings(private val context: Context) {
    private val sharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    private val backgroundKey = "Background"

    fun saveBackground(background: String) {
        val editor = sharedPref.edit()
        editor.putString(backgroundKey, background)
        editor.apply()
    }

    fun getBackground(): String? {
        return sharedPref.getString(backgroundKey, null)
    }
}