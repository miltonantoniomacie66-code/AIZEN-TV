package com.aizentv.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : Activity() {

    private var bg = Color.rgb(8, 8, 13)
    private var panel = Color.rgb(22, 20, 31)
    private var panel2 = Color.rgb(31, 27, 43)
    private val purple = Color.rgb(168, 85, 247)
    private val purpleDark = Color.rgb(91, 33, 182)
    private var white = Color.WHITE
    private var muted = Color.rgb(164, 160, 178)
    private val green = Color.rgb(65, 190, 140)

    private lateinit var content: LinearLayout
    private lateinit var navigation: LinearLayout
    private var selectedCategory = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyAizenTheme()
        window.statusBarColor = bg
        window.navigationBarColor = bg
        if (FirebaseAuth.getInstance().currentUser?.isEmailVerified != true) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }
        showHome()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }

    private fun gradient(
        colors: IntArray,
        radius: Int = 22
    ): GradientDrawable =
        GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            colors
        ).apply {
            cornerRadius = dp(radius).toFloat()
        }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
        includeFontPadding = true
    }

    private fun heading(value: String): TextView =
        text(value, 20f, white, true).apply {
            setPadding(0, dp(25), 0, dp(5))
        }

    private fun description(value: String): TextView =
        text(value, 13f, muted).apply {
            setLineSpacing(dp(2).toFloat(), 1f)
            setPadding(0, 0, 0, dp(12))
        }

    private fun smallLabel(value: String): TextView =
        text(value, 10f, purple, true).apply {
            letterSpacing = 0.12f
            setPadding(0, 0, 0, dp(8))
        }

    private fun button(
        value: String,
        action: () -> Unit
    ): TextView = text(value, 13f, white, true).apply {
        gravity = Gravity.CENTER
        background = rounded(purple, 13)
        setPadding(dp(18), dp(14), dp(18), dp(14))
        setOnClickListener { action() }
    }

    private fun card(
        title: String,
        subtitle: String
    ): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = rounded(panel, 17)
            elevation = dp(3).toFloat()
            clipToOutline = true
            addView(text(title, 15f, white, true))
            addView(text(subtitle, 12f, muted).apply {
                setPadding(0, dp(6), 0, 0)
                setLineSpacing(dp(2).toFloat(), 1f)
            })
        }

    private fun showHome() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(18), dp(12), dp(18), dp(4))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        brand.addView(text("AIZEN", 25f, white, true))
        brand.addView(text(" TV", 25f, purple, true))
        header.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))

        header.addView(
            text("FREE", 10f, green, true).apply {
                background = rounded(Color.rgb(18, 48, 39), 20)
                setPadding(dp(12), dp(7), dp(12), dp(7))
            }
        )
        root.addView(header)

        root.addView(
            text("O TEU UNIVERSO DE ENTRETENIMENTO",
                9f, muted, true).apply {
                letterSpacing = 0.12f
                setPadding(0, dp(4), 0, dp(12))
            }
        )

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(22))
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = rounded(panel, 18)
            setPadding(dp(3), dp(3), dp(3), dp(3))
        }
        root.addView(navigation)

        setContentView(root)
        showPage("Início")
    }

    private fun showPage(page: String) {
        content.removeAllViews()

        when (page) {
            "Início" -> buildHome()
            "Explorar" -> buildExplore()
            "Ao Vivo" -> buildLive()
            "Downloads" -> buildDownloads()
            "Perfil" -> buildProfile()
        }

        buildNavigation(page)
    }

    private fun sectionTitle(title: String, action: String? = null,
                             onAction: (() -> Unit)? = null) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(
            text(title, 18f, white, true),
            LinearLayout.LayoutParams(0, -2, 1f)
        )
        if (action != null && onAction != null) {
            row.addView(text(action, 11f, purple, true).apply {
                setPadding(dp(8), dp(8), 0, dp(8))
                setOnClickListener { onAction() }
            })
        }
        content.addView(row)
    }

    private fun buildHome() {
        content.addView(smallLabel("BEM-VINDO AO AIZEN TV"))

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(21), dp(23), dp(21), dp(21))
            background = gradient(
                intArrayOf(
                    Color.rgb(91, 38, 153),
                    Color.rgb(48, 28, 78),
                    Color.rgb(20, 19, 29)
                )
            )
            elevation = dp(8).toFloat()
            clipToOutline = true
            minimumHeight = dp(205)
        }

        hero.addView(
            text("AIZEN TV  •  O TEU UNIVERSO",
                10f, Color.rgb(222, 194, 255), true).apply {
                letterSpacing = 0.12f
                setPadding(0, 0, 0, dp(15))
            }
        )

        hero.addView(
            text("A TUA PRÓXIMA\nHISTÓRIA COMEÇA AQUI",
                27f, white, true).apply {
                setLineSpacing(dp(3).toFloat(), 1f)
            }
        )

        hero.addView(
            text("Filmes, séries e anime num só universo.",
                13f, Color.rgb(225, 214, 239)).apply {
                setPadding(0, dp(10), 0, dp(18))
            }
        )

        hero.addView(button("EXPLORAR CATÁLOGO  →") {
            showPage("Explorar")
        })

        content.addView(hero)
        hero.alpha = 0f
        hero.translationY = dp(12).toFloat()
        hero.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(380)
            .start()

        val searchShortcut = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = rounded(panel, 14)
            setPadding(dp(15), dp(14), dp(15), dp(14))
            addView(text("⌕", 23f, purple, true))
            addView(text("  Procurar filmes e conteúdos...", 13f, muted))
            setOnClickListener { showPage("Explorar") }
        }
        content.addView(searchShortcut,
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(17)
            }
        )

        sectionTitle("Explora por universo")

        val universes = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        addUniverse(
            universes, "FILMES", "Cinema",
            intArrayOf(Color.rgb(90, 42, 130), Color.rgb(38, 25, 58)),
            "Filmes"
        )
        addUniverse(
            universes, "SÉRIES", "Episódios",
            intArrayOf(Color.rgb(30, 70, 100), Color.rgb(20, 30, 48)),
            "Séries"
        )
        addUniverse(
            universes, "ANIME", "Animação",
            intArrayOf(Color.rgb(120, 45, 82), Color.rgb(48, 24, 53)),
            "Anime"
        )

        val universeScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(universes)
        }
        content.addView(universeScroll)

        sectionTitle("Descobre o AIZEN")
        content.addView(description(
            "Estamos a construir um catálogo com fontes identificadas e conteúdos cuja utilização é autorizada."
        ))

        val spotlight = card(
            "AIZEN Spotlight",
            "Os destaques serão apresentados aqui quando o catálogo visual estiver ligado."
        )
        spotlight.background = gradient(
            intArrayOf(Color.rgb(38, 29, 54), Color.rgb(22, 20, 31)),
            17
        )
        content.addView(spotlight)

        sectionTitle("A minha lista", "VER LISTA") {
            showFavorites()
        }

        val favorites = LibraryStore.favorites(this)
        if (favorites.isEmpty()) {
            content.addView(card(
                "A tua lista está vazia",
                "Guarda os teus conteúdos favoritos para os encontrares aqui."
            ))
        } else {
            content.addView(card(
                "${favorites.size} conteúdos guardados",
                "Os teus favoritos estão guardados neste dispositivo."
            ))
        }

        content.addView(
            text("AIZEN TV  •  ENTRETENIMENTO NUM SÓ LUGAR",
                9f, muted).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(25), 0, dp(8))
                letterSpacing = 0.08f
            }
        )
    }

    private fun addUniverse(
        parent: LinearLayout,
        title: String,
        subtitle: String,
        colors: IntArray,
        destination: String
    ) {
        val tile = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = gradient(colors, 17)
            elevation = dp(4).toFloat()
            clipToOutline = true
            addView(text("✦", 25f, white, true))
            addView(text(title, 13f, white, true).apply {
                setPadding(0, dp(15), 0, dp(3))
            })
            addView(text(subtitle, 10f, Color.rgb(220, 211, 231)))
            setOnClickListener {
                when (destination) {
                    "Filmes" -> showPage("Explorar")
                    "Séries", "Anime" -> buildDiscovery(destination)
                    else -> showUnavailableUniverse(destination)
                }
            }
        }

        parent.addView(tile, LinearLayout.LayoutParams(dp(145), dp(145)).apply {
            rightMargin = dp(10)
        })
    }

    private fun showUnavailableUniverse(name: String) {
        content.removeAllViews()
        content.addView(smallLabel("AIZEN TV • CATÁLOGO"))
        content.addView(heading(name))
        content.addView(description(
            "Esta categoria ainda não tem uma fonte de conteúdos integrada. " +
            "Não vamos apresentar títulos ou transmissões que não estejam disponíveis."
        ))
        content.addView(card(
            "Categoria em desenvolvimento",
            "A integração será feita numa próxima etapa do projeto."
        ))
        content.addView(button("VOLTAR AO INÍCIO") {
            showPage("Início")
        })
    }

    private fun showFavorites() {
        content.removeAllViews()
        content.addView(smallLabel("A TUA BIBLIOTECA"))
        content.addView(heading("A minha lista"))

        val favorites = LibraryStore.favorites(this)
        if (favorites.isEmpty()) {
            content.addView(card(
                "Ainda não tens favoritos",
                "Quando guardares um filme, ele aparecerá nesta área."
            ))
        } else {
            favorites.forEach { id ->
                val saved = LibraryStore.getMedia(this, id)
                val title = saved?.title?.takeIf { it.isNotBlank() } ?: id
                val details = listOfNotNull(
                    saved?.creator?.takeIf { it.isNotBlank() },
                    saved?.year?.takeIf { it.isNotBlank() }
                ).joinToString(" • ").ifBlank { "Conteúdo guardado na tua biblioteca." }

                val itemCard = card(title, details)
                itemCard.setOnClickListener {
                    val url = saved?.sourceUrl
                    if (!url.isNullOrBlank()) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }
                content.addView(itemCard, LinearLayout.LayoutParams(-1, -2).apply {
                    bottomMargin = dp(9)
                })
            }
        }

        content.addView(button("VER HISTÓRICO") {
            showHistory()
        })

        content.addView(button("VOLTAR") {
            showPage("Início")
        })
    }

    private fun showHistory() {
        content.removeAllViews()
        content.addView(smallLabel("A TUA BIBLIOTECA"))
        content.addView(heading("Histórico"))
        content.addView(description("Os conteúdos aparecem aqui quando o Player guardar o progresso."))

        val history = LibraryStore.history(this)
        if (history.isEmpty()) {
            content.addView(card(
                "Histórico vazio",
                "Quando começares a assistir a um conteúdo compatível, o progresso ficará registado aqui."
            ))
        } else {
            history.forEach { progress ->
                val saved = LibraryStore.getMedia(this, progress.mediaId)
                val title = saved?.title?.takeIf { it.isNotBlank() } ?: progress.mediaId
                val position = formatPlaybackTime(progress.positionMs)
                val duration = if (progress.durationMs > 0)
                    formatPlaybackTime(progress.durationMs) else "desconhecida"
                val percent = if (progress.durationMs > 0)
                    ((progress.positionMs * 100) / progress.durationMs).coerceIn(0, 100) else 0

                content.addView(card(
                    title,
                    "Progresso: $position de $duration • $percent%"
                ), LinearLayout.LayoutParams(-1, -2).apply {
                    bottomMargin = dp(9)
                })
            }
        }

        content.addView(button("VOLTAR À MINHA LISTA") {
            showFavorites()
        })
    }

    private fun formatPlaybackTime(milliseconds: Long): String {
        val totalSeconds = milliseconds.coerceAtLeast(0) / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
        else "%02d:%02d".format(minutes, seconds)
    }
    private fun loadCover(
        identifier: String,
        target: android.widget.ImageView
    ) {
        target.tag = identifier

        Thread {
            var bitmap: android.graphics.Bitmap? = null
            var connection: java.net.HttpURLConnection? = null

            try {
                val address =
                    "https://archive.org/services/img/$identifier"

                connection = java.net.URL(address)
                    .openConnection() as java.net.HttpURLConnection

                connection.connectTimeout = 12000
                connection.readTimeout = 15000
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode in 200..299) {
                    connection.inputStream.use {
                        bitmap = android.graphics.BitmapFactory.decodeStream(it)
                    }
                }
            } catch (_: Exception) {
                // Mantém a capa de substituição se a imagem não carregar.
            } finally {
                connection?.disconnect()
            }

            val result = bitmap

            android.os.Handler(
                android.os.Looper.getMainLooper()
            ).post {
                if (target.tag == identifier && result != null) {
                    target.setImageBitmap(result)
                }
            }
        }.start()
    }


    private fun loadRemoteImage(
        imageUrl: String,
        target: android.widget.ImageView
    ) {
        target.tag = imageUrl
        if (!imageUrl.startsWith("https://")) return

        Thread {
            var bitmap: android.graphics.Bitmap? = null
            var connection: java.net.HttpURLConnection? = null
            try {
                connection = java.net.URL(imageUrl)
                    .openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 12000
                connection.readTimeout = 15000
                connection.setRequestProperty("User-Agent", "AIZEN-TV Android")
                if (connection.responseCode in 200..299) {
                    connection.inputStream.use {
                        bitmap = android.graphics.BitmapFactory.decodeStream(it)
                    }
                }
            } catch (_: Exception) {
            } finally {
                connection?.disconnect()
            }

            val result = bitmap
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                if (target.tag == imageUrl && result != null) {
                    target.setImageBitmap(result)
                }
            }
        }.start()
    }

    private fun buildDiscovery(category: String) {
        content.removeAllViews()
        content.addView(text("←  VOLTAR AO INÍCIO", 12f, purple, true).apply {
            setPadding(0, dp(12), 0, dp(18))
            setOnClickListener { showPage("Início") }
        })
        content.addView(smallLabel("AIZEN TV • $category"))
        content.addView(heading("Explorar $category"))
        content.addView(description(
            "Descobre títulos, capas e informações através de fontes externas."
        ))

        val search = EditText(this).apply {
            hint = if (category == "Anime") "Pesquisar anime..."
                else "Pesquisar séries..."
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 14f
            setSingleLine(true)
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setPadding(dp(15), dp(13), dp(15), dp(13))
            background = rounded(panel, 14)
        }
        content.addView(search)

        val results = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        fun performSearch(query: String) {
            results.removeAllViews()
            results.addView(description("A procurar $category..."))

            DiscoveryRepository.search(category, query) { response ->
                results.removeAllViews()
                response.onSuccess { items ->
                    if (items.isEmpty()) {
                        results.addView(card(
                            "Nenhum resultado encontrado",
                            "Experimenta outro título ou tenta novamente mais tarde."
                        ))
                    } else {
                        results.addView(text(
                            "${items.size} RESULTADOS",
                            10f, purple, true
                        ).apply {
                            setPadding(0, dp(16), 0, dp(10))
                        })

                        items.forEach { item ->
                            val cover = android.widget.ImageView(this).apply {
                                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                                background = rounded(panel2, 12)
                                contentDescription = "Capa de ${item.title}"
                            }
                            loadRemoteImage(item.imageUrl, cover)

                            val info = LinearLayout(this).apply {
                                orientation = LinearLayout.VERTICAL
                                addView(text(item.title, 15f, white, true))
                                addView(text(
                                    item.subtitle,
                                    11f, purple
                                ).apply {
                                    setPadding(0, dp(6), 0, dp(7))
                                })
                                addView(text(
                                    item.description.ifBlank {
                                        "Descrição não disponível."
                                    }.take(180),
                                    11f, muted
                                ))
                                addView(text(
                                    "VER INFORMAÇÕES  →",
                                    10f, purple, true
                                ).apply {
                                    setPadding(0, dp(10), 0, 0)
                                })
                            }

                            val row = LinearLayout(this).apply {
                                orientation = LinearLayout.HORIZONTAL
                                gravity = Gravity.CENTER_VERTICAL
                                background = rounded(panel, 16)
                                setPadding(dp(11), dp(11), dp(11), dp(11))
                                addView(cover,
                                    LinearLayout.LayoutParams(dp(94), dp(132))
                                )
                                addView(info,
                                    LinearLayout.LayoutParams(0, -2, 1f).apply {
                                        leftMargin = dp(12)
                                    }
                                )
                                setOnClickListener {
                                    buildDiscoveryDetails(item)
                                }
                            }

                            results.addView(row,
                                LinearLayout.LayoutParams(-1, -2).apply {
                                    bottomMargin = dp(10)
                                }
                            )
                        }
                    }
                }.onFailure {
                    results.addView(card(
                        "Não foi possível carregar",
                        "Verifica a ligação à Internet e tenta novamente."
                    ))
                }
            }
        }

        content.addView(button("PESQUISAR") {
            performSearch(search.text.toString())
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(10)
        })

        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(search.text.toString())
                true
            } else false
        }

        content.addView(results,
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(16)
            }
        )
        performSearch("")
    }

    private fun buildDiscoveryDetails(item: DiscoveryItem) {
        content.removeAllViews()

        content.addView(text("←  VOLTAR A ${item.category.uppercase()}",
            12f, purple, true).apply {
            setPadding(0, dp(12), 0, dp(18))
            setOnClickListener { buildDiscovery(item.category) }
        })

        content.addView(smallLabel("AIZEN TV • ${item.category.uppercase()}"))

        val cover = android.widget.ImageView(this).apply {
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            background = rounded(panel, 14)
            contentDescription = "Capa de ${item.title}"
        }
        loadRemoteImage(item.imageUrl, cover)
        content.addView(cover,
            LinearLayout.LayoutParams(dp(155), dp(220)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = dp(14)
            }
        )

        content.addView(heading(item.title))
        content.addView(description(item.subtitle))
        content.addView(card(
            "Sinopse",
            item.description.ifBlank { "Descrição não disponível." }
        ))
        content.addView(description(
            "Informações fornecidas por ${if (item.category == "Anime") "AniList" else "TVmaze"}. " +
            "Esta fonte apresenta metadados e não fornece, por si só, autorização para reprodução."
        ))
        content.addView(button("CONSULTAR FONTE ORIGINAL  ↗") {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse(item.sourceUrl)
            ))
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(10)
        })
    }

    private fun buildExplore() {
        content.addView(smallLabel("AIZEN TV • CATÁLOGO"))
        content.addView(heading("Explorar"))
        content.addView(description(
            "Pesquisa filmes, séries e anime num só lugar."
        ))

        val categories = listOf("Todos", "Filmes", "Séries", "Anime")
        val categoryButtons = mutableMapOf<String, TextView>()

        val categoryRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        fun updateCategoryButtons() {
            categoryButtons.forEach { (name, view) ->
                val selected = name == selectedCategory
                view.background = rounded(
                    if (selected) purpleDark else panel,
                    18
                )
                view.setTextColor(if (selected) white else muted)
            }
        }

        categories.forEach { category ->
            val chip = text(
                category,
                12f,
                muted,
                true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(dp(16), dp(11), dp(16), dp(11))
                background = rounded(panel, 18)
                setOnClickListener {
                    selectedCategory = category
                    updateCategoryButtons()
                }
            }

            categoryButtons[category] = chip
            categoryRow.addView(
                chip,
                LinearLayout.LayoutParams(-2, -2).apply {
                    rightMargin = dp(8)
                }
            )
        }

        updateCategoryButtons()

        val categoryScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(categoryRow)
        }
        content.addView(categoryScroll)

        val search = EditText(this).apply {
            hint = "Pesquisar por título..."
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 14f
            setSingleLine(true)
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setPadding(dp(15), dp(13), dp(15), dp(13))
            background = rounded(panel, 14)
        }

        content.addView(search,
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(14)
            }
        )

        val year = EditText(this).apply {
            hint = "Filtrar por ano (ex.: 2020)"
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 13f
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(dp(15), dp(12), dp(15), dp(12))
            background = rounded(panel, 14)
        }

        content.addView(year,
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(9)
            }
        )

        val results = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        fun matchesYear(value: String, selectedYear: String): Boolean {
            if (selectedYear.isBlank()) return true
            if (!selectedYear.matches(Regex("[0-9]{4}"))) return false
            return Regex("[0-9]{4}").find(value)?.value == selectedYear
        }

        var searchGeneration = 0

        fun addMovieSection(query: String, selectedYear: String, requestId: Int) {
            results.addView(heading("Filmes"))
            val section = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }
            results.addView(section)
            section.addView(description("A procurar filmes..."))

            CatalogRepository.search(query) callback@{ response ->
                if (requestId != searchGeneration) return@callback
                section.removeAllViews()

                response.onSuccess { items ->
                    val filtered = items.filter {
                        matchesYear(it.year, selectedYear)
                    }.take(30)

                    if (filtered.isEmpty()) {
                        section.addView(card(
                            "Sem filmes encontrados",
                            "Experimenta outro título ou ano."
                        ))
                    } else {
                        section.addView(text(
                            "${filtered.size} RESULTADOS",
                            10f, purple, true
                        ).apply {
                            setPadding(0, 0, 0, dp(10))
                        })

                        filtered.forEach { item ->
                            val cover = android.widget.ImageView(this).apply {
                                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                                background = rounded(panel2, 12)
                                contentDescription = "Capa de ${item.title}"
                            }
                            loadCover(item.identifier, cover)

                            val info = LinearLayout(this).apply {
                                orientation = LinearLayout.VERTICAL
                                addView(text(item.title, 14f, white, true))
                                addView(text(
                                    "${item.year} • ${item.durationLabel}",
                                    11f, purple
                                ).apply {
                                    setPadding(0, dp(6), 0, dp(6))
                                })
                                addView(text(
                                    "Licença declarada: ${item.licenseLabel}",
                                    10f, muted
                                ))
                                addView(text(
                                    "VER DETALHES →",
                                    10f, purple, true
                                ).apply {
                                    setPadding(0, dp(9), 0, 0)
                                })
                            }

                            val row = LinearLayout(this).apply {
                                orientation = LinearLayout.HORIZONTAL
                                gravity = Gravity.CENTER_VERTICAL
                                background = rounded(panel, 16)
                                setPadding(dp(11), dp(11), dp(11), dp(11))
                                addView(cover,
                                    LinearLayout.LayoutParams(dp(94), dp(132))
                                )
                                addView(info,
                                    LinearLayout.LayoutParams(0, -2, 1f).apply {
                                        leftMargin = dp(12)
                                    }
                                )
                                setOnClickListener {
                                    buildMovieDetails(item)
                                }
                            }

                            section.addView(row,
                                LinearLayout.LayoutParams(-1, -2).apply {
                                    bottomMargin = dp(10)
                                }
                            )
                        }
                    }
                }.onFailure {
                    section.addView(card(
                        "Erro ao carregar filmes",
                        "Verifica a ligação e tenta novamente."
                    ))
                }
            }
        }

        fun addDiscoverySection(
            category: String,
            query: String,
            selectedYear: String,
            requestId: Int
        ) {
            results.addView(heading(category))
            val section = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }
            results.addView(section)
            section.addView(description("A procurar $category..."))

            DiscoveryRepository.search(category, query) callback@{ response ->
                if (requestId != searchGeneration) return@callback
                section.removeAllViews()

                response.onSuccess { items ->
                    val filtered = items.filter {
                        matchesYear(it.subtitle, selectedYear)
                    }.take(30)

                    if (filtered.isEmpty()) {
                        section.addView(card(
                            "Sem resultados",
                            "Não encontrámos títulos desta categoria para os filtros escolhidos."
                        ))
                    } else {
                        section.addView(text(
                            "${filtered.size} RESULTADOS",
                            10f, purple, true
                        ).apply {
                            setPadding(0, 0, 0, dp(10))
                        })

                        filtered.forEach { item ->
                            val cover = android.widget.ImageView(this).apply {
                                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                                background = rounded(panel2, 12)
                                contentDescription = "Capa de ${item.title}"
                            }
                            loadRemoteImage(item.imageUrl, cover)

                            val info = LinearLayout(this).apply {
                                orientation = LinearLayout.VERTICAL
                                addView(text(item.title, 14f, white, true))
                                addView(text(
                                    item.subtitle,
                                    11f, purple
                                ).apply {
                                    setPadding(0, dp(6), 0, dp(7))
                                })
                                addView(text(
                                    item.description.ifBlank {
                                        "Descrição não disponível."
                                    }.take(180),
                                    11f, muted
                                ))
                                addView(text(
                                    "VER INFORMAÇÕES →",
                                    10f, purple, true
                                ).apply {
                                    setPadding(0, dp(9), 0, 0)
                                })
                            }

                            val row = LinearLayout(this).apply {
                                orientation = LinearLayout.HORIZONTAL
                                gravity = Gravity.CENTER_VERTICAL
                                background = rounded(panel, 16)
                                setPadding(dp(11), dp(11), dp(11), dp(11))
                                addView(cover,
                                    LinearLayout.LayoutParams(dp(94), dp(132))
                                )
                                addView(info,
                                    LinearLayout.LayoutParams(0, -2, 1f).apply {
                                        leftMargin = dp(12)
                                    }
                                )
                                setOnClickListener {
                                    buildDiscoveryDetails(item)
                                }
                            }

                            section.addView(row,
                                LinearLayout.LayoutParams(-1, -2).apply {
                                    bottomMargin = dp(10)
                                }
                            )
                        }
                    }
                }.onFailure {
                    section.addView(card(
                        "Erro ao carregar $category",
                        "Verifica a ligação à Internet e tenta novamente."
                    ))
                }
            }
        }

        fun performSearch() {
            val query = search.text.toString().trim()
            val selectedYear = year.text.toString().trim()
            searchGeneration++
            val requestId = searchGeneration

            results.removeAllViews()

            if (selectedYear.isNotBlank() &&
                !selectedYear.matches(Regex("[0-9]{4}"))) {
                results.addView(card(
                    "Ano inválido",
                    "Introduz um ano com quatro algarismos."
                ))
                return
            }

            when (selectedCategory) {
                "Filmes" -> addMovieSection(query, selectedYear, requestId)
                "Séries" -> addDiscoverySection("Séries", query, selectedYear, requestId)
                "Anime" -> addDiscoverySection("Anime", query, selectedYear, requestId)
                else -> {
                    addMovieSection(query, selectedYear, requestId)
                    addDiscoverySection("Séries", query, selectedYear, requestId)
                    addDiscoverySection("Anime", query, selectedYear, requestId)
                }
            }
        }

        content.addView(button("PESQUISAR") {
            performSearch()
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(10)
        })

        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        content.addView(results,
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(12)
            }
        )

        performSearch()
    }

    private fun buildMovieDetails(item: CatalogItem) {
        content.removeAllViews()

        content.addView(text("←  VOLTAR AO CATÁLOGO",
            12f, purple, true).apply {
            setPadding(0, dp(12), 0, dp(20))
            setOnClickListener { showPage("Explorar") }
        })

        content.addView(smallLabel("AIZEN TV • DETALHES"))
        val detailCover = android.widget.ImageView(this).apply {
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            background = rounded(panel, 14)
            contentDescription = "Capa de ${item.title}"
        }

        loadCover(item.identifier, detailCover)

        content.addView(detailCover,
            LinearLayout.LayoutParams(dp(150), dp(215)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = dp(16)
            }
        )

        content.addView(heading(item.title))
        content.addView(description(
            "${item.year}  •  ${item.durationLabel}"
        ))

        content.addView(card(
            "Sobre este conteúdo",
            "Autor: ${item.creator}\n" +
            "Duração declarada: ${item.durationLabel}\n" +
            "Licença declarada: ${item.licenseLabel}"
        ))

        LibraryStore.saveMedia(this, SavedMedia(
            mediaId = item.identifier,
            title = item.title,
            creator = item.creator,
            year = item.year,
            imageUrl = "https://archive.org/services/img/${item.identifier}",
            sourceUrl = item.sourceUrl
        ))

        val favorite = LibraryStore.isFavorite(this, item.identifier)
        content.addView(button(
            if (favorite) "✓  REMOVER DOS FAVORITOS"
            else "＋  ADICIONAR À MINHA LISTA"
        ) {
            if (LibraryStore.isFavorite(this, item.identifier)) {
                LibraryStore.removeFavorite(this, item.identifier)
            } else {
                LibraryStore.addFavorite(this, item.identifier)
            }
            buildMovieDetails(item)
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(15)
        })

        content.addView(heading("Fonte e disponibilidade"))
        content.addView(description(
            "A fonte declara esta licença, mas os direitos e a disponibilidade " +
            "da reprodução devem ser confirmados antes de integrar o vídeo."
        ))

        content.addView(button("▶  REPRODUZIR CONTEÚDO") {
            CatalogRepository.resolvePlayableVideo(item) { result ->
                result.fold(
                    onSuccess = { videoUrl ->
                        startActivity(Intent(
                            this,
                            PlayerActivity::class.java
                        ).apply {
                            putExtra(PlayerActivity.EXTRA_MEDIA_URL, videoUrl)
                            putExtra(PlayerActivity.EXTRA_MEDIA_ID, item.identifier)
                            putExtra(PlayerActivity.EXTRA_TITLE, item.title)
                        })
                    },
                    onFailure = { error ->
                        android.widget.Toast.makeText(
                            this,
                            error.message ?: "Conteúdo indisponível para reprodução.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(18)
        })

        content.addView(button("CONSULTAR FONTE ORIGINAL  ↗") {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse(item.sourceUrl)
            ))
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(8)
        })
    }

    private fun buildLive() {
        content.addView(smallLabel("TRANSMISSÕES OFICIAIS"))
        content.addView(heading("Ao Vivo"))
        content.addView(description(
            "Acompanha emissões de canais através das plataformas oficiais."
        ))

        content.addView(card(
            "TVM — Televisão de Moçambique",
            "Acede à emissão da TVM e da TVM Internacional no portal oficial."
        ))

        content.addView(button("ABRIR TVM OFICIAL  ↗") {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://online.tvm.co.mz/")
            ))
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(8)
        })

        content.addView(card(
            "MBC — Mozambique Broadcasting Company",
            "Consulta a emissão ao vivo e a programação disponibilizada pela MBC."
        ), LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(18)
        })

        content.addView(button("ABRIR MBC AO VIVO  ↗") {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://mbc.co.mz/tv-ao-vivo/")
            ))
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(8)
        })

        content.addView(description(
            "As transmissões são abertas nas plataformas oficiais. " +
            "A disponibilidade depende de cada emissora."
        ))
    }



    private fun applyAizenTheme() {
        val dark = AizenPreferences.isDark(this)
        bg = if (dark) Color.rgb(8, 8, 13) else Color.rgb(246, 244, 250)
        panel = if (dark) Color.rgb(22, 20, 31) else Color.rgb(255, 255, 255)
        panel2 = if (dark) Color.rgb(31, 27, 43) else Color.rgb(235, 230, 243)
        white = if (dark) Color.WHITE else Color.rgb(30, 25, 39)
        muted = if (dark) Color.rgb(164, 160, 178) else Color.rgb(105, 96, 117)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            window.decorView.systemUiVisibility =
                if (dark) 0 else
                    android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    private fun buildProfile() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        content.addView(smallLabel("AIZEN TV"))
        content.addView(heading("O teu perfil"))
        content.addView(description(
            "Gere a tua conta e as definições de segurança."
        ))

        val email = user?.email ?: "Email não disponível"
        val verified = user?.isEmailVerified == true

        content.addView(card(
            "A MINHA CONTA",
            email
        ))

        content.addView(card(
            "VERIFICAÇÃO DO EMAIL",
            if (verified) {
                "Email confirmado. A tua conta está verificada."
            } else {
                "O email ainda não está confirmado. Verifica a tua caixa de entrada."
            }
        ), LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(10)
        })

        content.addView(button("ATUALIZAR ESTADO DO EMAIL") {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            } else {
                currentUser.reload().addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        showPage("Perfil")
                    } else {
                        android.widget.Toast.makeText(
                            this,
                            "Não foi possível atualizar o estado da conta.",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(10)
        })

        content.addView(heading("SEGURANÇA"))

        content.addView(button("RECUPERAR PALAVRA-PASSE") {
            val accountEmail = auth.currentUser?.email
            if (accountEmail.isNullOrBlank()) {
                android.widget.Toast.makeText(
                    this,
                    "Não existe um email associado à conta.",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } else {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Recuperar palavra-passe")
                    .setMessage("Enviar instruções de recuperação para $accountEmail?")
                    .setNegativeButton("CANCELAR", null)
                    .setPositiveButton("ENVIAR") { _, _ ->
                        auth.sendPasswordResetEmail(accountEmail)
                            .addOnSuccessListener {
                                android.widget.Toast.makeText(
                                    this,
                                    "Email de recuperação enviado.",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                            .addOnFailureListener {
                                android.widget.Toast.makeText(
                                    this,
                                    it.localizedMessage ?: "Não foi possível enviar o email.",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                    .show()
            }
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(8)
        })

        content.addView(heading("PREFERÊNCIAS"))

        val subtitleLanguage = AizenPreferences.subtitleLanguage(this)
        val subtitleLabel = when (subtitleLanguage) {
            "en" -> "Inglês"
            "off" -> "Desativadas"
            else -> "Português"
        }

        content.addView(card(
            "IDIOMA DAS LEGENDAS",
            subtitleLabel
        ))

        content.addView(button("ALTERAR LEGENDAS") {
            val options = arrayOf(
                "Português",
                "Inglês",
                "Desativadas"
            )
            val current = when (AizenPreferences.subtitleLanguage(this)) {
                "en" -> 1
                "off" -> 2
                else -> 0
            }

            android.app.AlertDialog.Builder(this)
                .setTitle("Idioma das legendas")
                .setSingleChoiceItems(options, current) { dialog, which ->
                    val language = when (which) {
                        1 -> "en"
                        2 -> "off"
                        else -> "pt"
                    }
                    AizenPreferences.setSubtitleLanguage(this, language)
                    dialog.dismiss()
                    showPage("Perfil")
                    android.widget.Toast.makeText(
                        this,
                        "Preferência de legendas guardada.",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("FECHAR", null)
                .show()
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(8)
        })


        val themeMode = AizenPreferences.themeMode(this)
        content.addView(card(
            "TEMA DA APLICAÇÃO",
            if (themeMode == "auto") "Automático (segue o sistema)" else "Escuro"
        ))
        content.addView(button("ALTERAR TEMA") {
            val options = arrayOf("Escuro", "Automático")
            val selected = if (AizenPreferences.themeMode(this) == "auto") 1 else 0
            android.app.AlertDialog.Builder(this)
                .setTitle("Tema da aplicação")
                .setSingleChoiceItems(options, selected) { dialog, which ->
                    AizenPreferences.setThemeMode(
                        this,
                        if (which == 1) "auto" else "dark"
                    )
                    dialog.dismiss()
                    recreate()
                }
                .setNegativeButton("FECHAR", null)
                .show()
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(8)
        })

        content.addView(heading("APLICAÇÃO"))

        val version = packageManager
            .getPackageInfo(packageName, 0).versionName ?: "1.0"

        content.addView(card(
            "AIZEN TV",
            "Versão $version\\nPlataforma de entretenimento."
        ))

        content.addView(button("TERMINAR SESSÃO") {
            android.app.AlertDialog.Builder(this)
                .setTitle("Terminar sessão?")
                .setMessage("Queres sair da tua conta AIZEN TV neste dispositivo?")
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("SAIR") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
                .show()
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(18)
        })
    }

    private fun buildPlaceholder(title: String, subtitle: String) {
        content.addView(smallLabel("AIZEN TV"))
        content.addView(heading(title))
        content.addView(description(subtitle))

        if (title == "O teu perfil") {
            val user = FirebaseAuth.getInstance().currentUser
            content.addView(card("Conta autenticada", user?.email ?: "Conta ativa"))
            content.addView(button("TERMINAR SESSÃO") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }, LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(12)
            })
        }

        content.addView(card(
            "Em desenvolvimento",
            "Esta secção será ligada às funcionalidades correspondentes nas próximas etapas."
        ))
    }


    private fun buildDownloads() {
        content.addView(smallLabel("AIZEN TV"))
        content.addView(heading("Os meus downloads"))
        content.addView(description(
            "Os teus conteúdos guardados para assistir offline."
        ))

        val folder = java.io.File(filesDir, "downloads")
        if (!folder.exists()) folder.mkdirs()

        val extensions = setOf("mp4", "mkv", "webm")
        val files = folder.listFiles()
            ?.filter { it.isFile && it.extension.lowercase() in extensions }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()

        val totalBytes = files.sumOf { it.length() }

        content.addView(card(
            "Armazenamento offline",
            "${files.size} ficheiro(s) guardado(s) • ${formatDownloadSize(totalBytes)}"
        ))

        if (files.isEmpty()) {
            content.addView(card(
                "Ainda sem downloads",
                "Quando existirem conteúdos autorizados guardados neste dispositivo, aparecerão aqui."
            ))
            return
        }

        files.forEach { file ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(15), dp(14), dp(15), dp(14))
                background = rounded(panel, 18)
            }

            val details = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                addView(text(file.name, 13f, white, true))
                addView(text(formatDownloadSize(file.length()), 11f, muted))
            }

            item.addView(details, LinearLayout.LayoutParams(0, -2, 1f))

            val remove = text("REMOVER", 10f, purple, true).apply {
                setPadding(dp(10), dp(12), 0, dp(12))
                setOnClickListener {
                    android.app.AlertDialog.Builder(this@MainActivity)
                        .setTitle("Remover download?")
                        .setMessage("Queres apagar ${file.name} deste dispositivo?")
                        .setNegativeButton("CANCELAR", null)
                        .setPositiveButton("REMOVER") { _, _ ->
                            if (file.delete()) {
                                showPage("Downloads")
                            } else {
                                android.widget.Toast.makeText(
                                    this@MainActivity,
                                    "Não foi possível remover o ficheiro.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .show()
                }
            }

            item.addView(remove)
            content.addView(
                item,
                LinearLayout.LayoutParams(-1, -2).apply {
                    bottomMargin = dp(10)
                }
            )
        }
    }

    private fun formatDownloadSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format(
            java.util.Locale.getDefault(),
            "%.1f MB",
            bytes / (1024.0 * 1024.0)
        )
    }

    private fun buildNavigation(page: String) {
        val items = listOf(
            Triple("⌂", "Início", "Início"),
            Triple("⌕", "Explorar", "Explorar"),
            Triple("▷", "Ao Vivo", "Ao Vivo"),
            Triple("↓", "Downloads", "Downloads"),
            Triple("◉", "Perfil", "Perfil")
        )

        navigation.removeAllViews()

        items.forEach { item ->
            val selected = item.second == page

            val tab = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(0, dp(7), 0, dp(7))

                addView(text(
                    item.first, 19f,
                    if (selected) purple else muted,
                    selected
                ).apply {
                    gravity = Gravity.CENTER
                })

                addView(text(
                    item.third, 9f,
                    if (selected) purple else muted,
                    selected
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(2), 0, 0)
                })

                setOnClickListener { showPage(item.second) }
            }

            navigation.addView(tab, LinearLayout.LayoutParams(0, -2, 1f))
        }
    }
}
