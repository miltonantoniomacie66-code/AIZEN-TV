package com.aizentv.app

import android.content.Context

object AizenPreferences {
    private const val PREFS = "aizen_preferences"
    private const val SUBTITLE_LANGUAGE = "subtitle_language"

    fun subtitleLanguage(context: Context): String {
        val saved = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(SUBTITLE_LANGUAGE, "pt")

        return if (saved in setOf("pt", "en", "off")) saved!! else "pt"
    }

    fun setSubtitleLanguage(context: Context, language: String) {
        require(language in setOf("pt", "en", "off"))
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(SUBTITLE_LANGUAGE, language)
            .apply()
    }
}
