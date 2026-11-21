package com.aizentv.app

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class CatalogItem(
    val identifier: String,
    val title: String,
    val creator: String,
    val year: String,
    val licenseUrl: String,
    val durationSeconds: Long
) {
    val sourceUrl: String
        get() = "https://archive.org/details/$identifier"

    val durationLabel: String
        get() {
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            return if (hours > 0) "${hours}h ${minutes}min"
            else "${minutes}min"
        }

    val licenseLabel: String
        get() = when {
            licenseUrl.contains("/publicdomain/zero/1.0") -> "CC0 1.0"
            licenseUrl.contains("/licenses/by-sa/4.0") -> "CC BY-SA 4.0"
            licenseUrl.contains("/licenses/by/4.0") -> "CC BY 4.0"
            else -> "Licença não confirmada"
        }
}

object CatalogRepository {

    private val mainHandler = Handler(Looper.getMainLooper())

    private fun readValue(value: Any?): String {
        return when (value) {
            is JSONArray -> value.optString(0, "")
            null -> ""
            else -> value.toString()
        }
    }

    private fun parseDuration(value: String): Long {
        val clean = value.trim()
        if (clean.isEmpty()) return 0

        clean.toLongOrNull()?.let { return it }

        val parts = clean.split(":")
        if (parts.size !in 2..3) return 0

        return try {
            val numbers = parts.map { it.toLong() }
            when (numbers.size) {
                2 -> numbers[0] * 60 + numbers[1]
                3 -> numbers[0] * 3600 + numbers[1] * 60 + numbers[2]
                else -> 0
            }
        } catch (_: Exception) {
            0
        }
    }

    fun search(
        query: String,
        callback: (Result<List<CatalogItem>>) -> Unit
    ) {
        Thread {
            try {
                val clean = query.trim()
                    .replace(Regex("[^\\p{L}\\p{N} ]"), " ")
                    .replace(Regex("\\s+"), " ")
                    .take(80)

                val searchQuery = if (clean.isEmpty()) {
                    "mediatype:movies"
                } else {
                    "mediatype:movies AND title:\"$clean\""
                }

                val encoded = URLEncoder.encode(searchQuery, "UTF-8")

                val address =
                    "https://archive.org/advancedsearch.php" +
                    "?q=$encoded" +
                    "&fl%5B%5D=identifier" +
                    "&fl%5B%5D=title" +
                    "&fl%5B%5D=creator" +
                    "&fl%5B%5D=year" +
                    "&fl%5B%5D=licenseurl" +
                    "&fl%5B%5D=runtime" +
                    "&rows=100&page=1&output=json"

                val connection =
                    URL(address).openConnection() as HttpURLConnection

                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.requestMethod = "GET"

                val response = try {
                    connection.inputStream.bufferedReader().use {
                        it.readText()
                    }
                } finally {
                    connection.disconnect()
                }

                val json = JSONObject(response)
                val docs = json.getJSONObject("response")
                    .getJSONArray("docs")

                val items = mutableListOf<CatalogItem>()

                for (i in 0 until docs.length()) {
                    val doc = docs.getJSONObject(i)

                    val license = readValue(
                        doc.opt("licenseurl")
                    ).lowercase()

                    val approved =
                        license.contains(
                            "creativecommons.org/publicdomain/zero/1.0"
                        ) ||
                        license.contains(
                            "creativecommons.org/licenses/by/4.0"
                        ) ||
                        license.contains(
                            "creativecommons.org/licenses/by-sa/4.0"
                        )

                    if (!approved) continue

                    val duration = parseDuration(
                        readValue(doc.opt("runtime"))
                    )

                    if (duration > 0 && duration < 3600) continue

                    val identifier = doc.optString("identifier", "")
                    if (identifier.isBlank()) continue

                    items.add(
                        CatalogItem(
                            identifier = identifier,
                            title = doc.optString("title", "Sem título"),
                            creator = readValue(doc.opt("creator")
                                ?: "Autor não indicado"),
                            year = readValue(doc.opt("year")
                                ?: "Ano desconhecido"),
                            licenseUrl = license,
                            durationSeconds = duration
                        )
                    )
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
}
