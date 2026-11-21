package com.aizentv.app

import android.content.Context
import org.json.JSONObject

data class WatchProgress(
    val mediaId: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long
)

object LibraryStore {

    private const val PREFS = "aizen_personal_library"
    private const val FAVORITES = "favorites"
    private const val PROGRESS_PREFIX = "progress_"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun addFavorite(context: Context, mediaId: String) {
        val values = favorites(context).toMutableSet()
        values.add(mediaId)
        prefs(context).edit()
            .putStringSet(FAVORITES, values)
            .apply()
    }

    fun removeFavorite(context: Context, mediaId: String) {
        val values = favorites(context).toMutableSet()
        values.remove(mediaId)
        prefs(context).edit()
            .putStringSet(FAVORITES, values)
            .apply()
    }

    fun isFavorite(context: Context, mediaId: String): Boolean =
        favorites(context).contains(mediaId)

    fun favorites(context: Context): Set<String> =
        prefs(context).getStringSet(FAVORITES, emptySet())
            ?.toSet() ?: emptySet()

    fun saveProgress(
        context: Context,
        mediaId: String,
        positionMs: Long,
        durationMs: Long
    ) {
        val data = JSONObject()
            .put("mediaId", mediaId)
            .put("positionMs", positionMs.coerceAtLeast(0))
            .put("durationMs", durationMs.coerceAtLeast(0))
            .put("updatedAt", System.currentTimeMillis())

        prefs(context).edit()
            .putString(PROGRESS_PREFIX + mediaId, data.toString())
            .putString("last_watched", mediaId)
            .apply()
    }

    fun getProgress(
        context: Context,
        mediaId: String
    ): WatchProgress? {
        val raw = prefs(context).getString(
            PROGRESS_PREFIX + mediaId,
            null
        ) ?: return null

        return try {
            val data = JSONObject(raw)
            WatchProgress(
                mediaId = data.getString("mediaId"),
                positionMs = data.optLong("positionMs"),
                durationMs = data.optLong("durationMs"),
                updatedAt = data.optLong("updatedAt")
            )
        } catch (_: Exception) {
            null
        }
    }

    fun lastWatched(context: Context): String? =
        prefs(context).getString("last_watched", null)
}
