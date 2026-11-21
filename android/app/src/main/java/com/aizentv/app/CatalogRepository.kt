package com.aizentv.app

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class CatalogItem(
    val identifier: String,
    val title: String,
    val creator: String,
    val year: String,
    val licenseUrl: String
) {
    val sourceUrl: String
        get() = "https://archive.org/details/$identifier"

    val licenseLabel: String
        get() = when {
            licenseUrl.contains("/publicdomain/zero/1.0") -> "CC0 1.0"
            licenseUrl.contains("/licenses/by-sa/4.0") -> "CC BY-SA 4.0"
            else -> "CC BY 4.0"
        }
}

object CatalogRepository {

    private val mainHandler = Handler(Looper.getMainLooper())

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
                    "&rows=40&page=1&output=json"

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

                    val license = doc.optString("licenseurl", "")
                        .lowercase()

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

                    val identifier = doc.optString("identifier", "")
                    val title = doc.optString("title", "Sem título")

                    if (identifier.isBlank()) continue

                    items.add(
                        CatalogItem(
                            identifier = identifier,
                            title = title,
                            creator = doc.optString(
                                "creator", "Autor não indicado"
                            ),
                            year = doc.optString("year", "Ano desconhecido"),
                            licenseUrl = license
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
