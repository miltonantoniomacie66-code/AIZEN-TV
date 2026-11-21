package com.aizentv.app

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class DiscoveryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val imageUrl: String,
    val sourceUrl: String,
    val category: String
)

object DiscoveryRepository {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun search(
        category: String,
        query: String,
        callback: (Result<List<DiscoveryItem>>) -> Unit
    ) {
        Thread {
            try {
                val items = when (category) {
                    "Séries" -> searchSeries(query)
                    "Anime" -> searchAnime(query)
                    else -> emptyList()
                }
                mainHandler.post {
                    callback(Result.success(items))
                }
            } catch (error: Exception) {
                mainHandler.post {
                    callback(Result.failure(error))
                }
            }
        }.start()
    }

    private fun request(
        address: String,
        method: String = "GET",
        body: String? = null
    ): String {
        val connection = URL(address).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 15000
            connection.readTimeout = 20000
            connection.requestMethod = method
            connection.setRequestProperty("Accept", "application/json")

            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=utf-8"
                )
                connection.outputStream.use {
                    it.write(body.toByteArray(Charsets.UTF_8))
                }
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val response = stream?.bufferedReader()?.use { it.readText() } ?: ""
            if (code !in 200..299) {
                throw Exception("Erro HTTP $code")
            }
            return response
        } finally {
            connection.disconnect()
        }
    }

    private fun cleanHtml(value: String): String {
        return android.text.Html.fromHtml(
            value,
            android.text.Html.FROM_HTML_MODE_COMPACT
        ).toString().replace(Regex("\\s+"), " ").trim()
    }

    private fun searchSeries(query: String): List<DiscoveryItem> {
        val clean = query.trim().take(80)
        val address = if (clean.isBlank()) {
            "https://api.tvmaze.com/shows?page=0"
        } else {
            "https://api.tvmaze.com/search/shows?q=" +
                URLEncoder.encode(clean, "UTF-8")
        }

        val root = JSONArray(request(address))
        val items = mutableListOf<DiscoveryItem>()

        for (i in 0 until minOf(root.length(), 40)) {
            val entry = root.getJSONObject(i)
            val show = if (entry.has("show")) {
                entry.getJSONObject("show")
            } else {
                entry
            }

            val id = show.optString("id", "")
            val title = show.optString("name", "").trim()
            if (id.isBlank() || title.isBlank()) continue

            val image = show.optJSONObject("image")
            val imageUrl = image?.optString("medium", "")
                ?.takeIf { it.startsWith("https://") } ?: ""

            val premiered = show.optString("premiered", "")
                .take(4).ifBlank { "Ano desconhecido" }

            val genres = show.optJSONArray("genres") ?: JSONArray()
            val genreText = (0 until genres.length())
                .map { genres.optString(it) }
                .filter { it.isNotBlank() }
                .take(3)
                .joinToString(" • ")
                .ifBlank { "Série" }

            items.add(
                DiscoveryItem(
                    id = id,
                    title = title,
                    subtitle = "$premiered • $genreText",
                    description = cleanHtml(show.optString(
                        "summary",
                        "Descrição não disponível."
                    )),
                    imageUrl = imageUrl,
                    sourceUrl = show.optString(
                        "url",
                        "https://www.tvmaze.com/"
                    ),
                    category = "Séries"
                )
            )
        }
        return items
    }

    private fun searchAnime(query: String): List<DiscoveryItem> {
        val clean = query.trim().take(80)

        val graph = """
            query (${'$'}search: String) {
              Page(page: 1, perPage: 30) {
                media(
                  search: ${'$'}search,
                  type: ANIME,
                  sort: POPULARITY_DESC
                ) {
                  id
                  title { romaji english }
                  description(asHtml: false)
                  episodes
                  seasonYear
                  genres
                  coverImage { large }
                  siteUrl
                  format
                }
              }
            }
        """.trimIndent()

        val variables = JSONObject().put(
            "search",
            if (clean.isBlank()) JSONObject.NULL else clean
        )

        val body = JSONObject()
            .put("query", graph)
            .put("variables", variables)
            .toString()

        val root = JSONObject(
            request("https://graphql.anilist.co", "POST", body)
        )

        val media = root.getJSONObject("data")
            .getJSONObject("Page")
            .getJSONArray("media")

        val items = mutableListOf<DiscoveryItem>()

        for (i in 0 until media.length()) {
            val anime = media.getJSONObject(i)
            val id = anime.optInt("id", 0)
            val titleObject = anime.optJSONObject("title") ?: JSONObject()
            val title = titleObject.optString("english")
                .ifBlank { titleObject.optString("romaji") }

            if (id <= 0 || title.isBlank()) continue

            val year = anime.optInt("seasonYear", 0)
                .takeIf { it > 0 }?.toString() ?: "Ano desconhecido"

            val episodes = anime.optInt("episodes", 0)
                .takeIf { it > 0 }?.let { "$it episódios" }
                ?: "Episódios não indicados"

            val genres = anime.optJSONArray("genres") ?: JSONArray()
            val genreText = (0 until genres.length())
                .map { genres.optString(it) }
                .take(3)
                .joinToString(" • ")
                .ifBlank { "Anime" }

            val cover = anime.optJSONObject("coverImage")
                ?.optString("large", "") ?: ""

            items.add(
                DiscoveryItem(
                    id = id.toString(),
                    title = title,
                    subtitle = "$year • $episodes • $genreText",
                    description = anime.optString(
                        "description",
                        "Descrição não disponível."
                    ),
                    imageUrl = cover,
                    sourceUrl = anime.optString(
                        "siteUrl",
                        "https://anilist.co/anime/$id"
                    ),
                    category = "Anime"
                )
            )
        }
        return items
    }
}
