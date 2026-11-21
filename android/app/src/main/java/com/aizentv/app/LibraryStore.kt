package com.aizentv.app

import android.content.Context
import org.json.JSONObject

data class WatchProgress(
    val mediaId: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long
)

data class SavedMedia(
    val mediaId: String,
    val title: String,
    val creator: String,
    val year: String,
    val imageUrl: String,
    val sourceUrl: String
)

object LibraryStore {

    private const val PREFS = "aizen_personal_library"
    private const val FAVORITES = "favorites"
    private const val PROGRESS_PREFIX = "progress_"
    private const val MEDIA_PREFIX = "media_"

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

    fun saveMedia(context: Context, media: SavedMedia) {
        val data = JSONObject()
            .put("mediaId", media.mediaId)
            .put("title", media.title)
            .put("creator", media.creator)
            .put("year", media.year)
            .put("imageUrl", media.imageUrl)
            .put("sourceUrl", media.sourceUrl)

        prefs(context).edit()
            .putString(MEDIA_PREFIX + media.mediaId, data.toString())
            .apply()
    }

    fun getMedia(context: Context, mediaId: String): SavedMedia? {
        val raw = prefs(context).getString(MEDIA_PREFIX + mediaId, null)
            ?: return null

        return try {
            val data = JSONObject(raw)
            SavedMedia(
                mediaId = data.getString("mediaId"),
                title = data.optString("title", mediaId),
                creator = data.optString("creator", ""),
                year = data.optString("year", ""),
                imageUrl = data.optString("imageUrl", ""),
                sourceUrl = data.optString("sourceUrl", "")
            )
        } catch (_: Exception) {
            null
        }
    }

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

    fun getProgress(context: Context, mediaId: String): WatchProgress? {
        val raw = prefs(context).getString(PROGRESS_PREFIX + mediaId, null)
            ?: return null

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

    fun history(context: Context): List<WatchProgress> {
        return prefs(context).all.keys
            .filter { it.startsWith(PROGRESS_PREFIX) }
            .mapNotNull { key ->
                val id = key.removePrefix(PROGRESS_PREFIX)
                getProgress(context, id)
            }
            .sortedByDescending { it.updatedAt }
    }

    fun lastWatched(context: Context): String? =
        prefs(context).getString("last_watched", null)

    fun clearProgress(context: Context, mediaId: String) {
        val editor = prefs(context).edit()
            .remove(PROGRESS_PREFIX + mediaId)

        if (lastWatched(context) == mediaId) {
            editor.remove("last_watched")
        }

        editor.apply()
    }
}
