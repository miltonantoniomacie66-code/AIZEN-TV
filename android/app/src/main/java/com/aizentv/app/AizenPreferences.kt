package com.aizentv.app

import android.content.Context

object AizenPreferences {
    private const val PREFS = "aizen_preferences"
    private const val SUBTITLE_LANGUAGE = "subtitle_language"
    private const val THEME_MODE = "theme_mode"

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

    fun themeMode(context: Context): String {
        val saved = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(THEME_MODE, "dark")
        return if (saved in setOf("dark", "auto")) saved!! else "dark"
    }

    fun setThemeMode(context: Context, mode: String) {
        require(mode in setOf("dark", "auto"))
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(THEME_MODE, mode)
            .apply()
    }

    fun isDark(context: Context): Boolean {
        if (themeMode(context) == "dark") return true
        val mask = context.resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return mask != android.content.res.Configuration.UI_MODE_NIGHT_NO
    }

}
