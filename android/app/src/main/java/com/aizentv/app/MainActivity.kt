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

class MainActivity : Activity() {

    private val bg = Color.rgb(8, 8, 13)
    private val panel = Color.rgb(22, 20, 31)
    private val panel2 = Color.rgb(31, 27, 43)
    private val purple = Color.rgb(168, 85, 247)
    private val purpleDark = Color.rgb(91, 33, 182)
    private val white = Color.WHITE
    private val muted = Color.rgb(164, 160, 178)
    private val green = Color.rgb(65, 190, 140)

    private lateinit var content: LinearLayout
    private lateinit var navigation: LinearLayout
    private var selectedCategory = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
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
            "Ao Vivo" -> buildPlaceholder(
                "Ao Vivo",
                "Uma área para transmissões de canais e eventos com fontes autorizadas."
            )
            "Downloads" -> buildPlaceholder(
                "Os meus downloads",
                "Aqui ficarão os conteúdos autorizados para guardar e assistir offline."
            )
            "Perfil" -> buildPlaceholder(
                "O teu perfil",
                "A tua conta, preferências e definições do AIZEN TV."
            )
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
                    Color.rgb(70, 28, 120),
                    Color.rgb(35, 23, 61),
                    Color.rgb(20, 19, 29)
                )
            )
        }

        hero.addView(
            text("A TUA PRÓXIMA\nHISTÓRIA COMEÇA AQUI",
                25f, white, true).apply {
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
                content.addView(card(
                    id,
                    "Conteúdo guardado na tua biblioteca."
                ), LinearLayout.LayoutParams(-1, -2).apply {
                    bottomMargin = dp(9)
                })
            }
        }

        content.addView(button("VOLTAR") {
            showPage("Início")
        })
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
            "Pesquisa conteúdos e consulta as informações da fonte original."
        ))

        val search = EditText(this).apply {
            hint = "Título, filme ou palavra-chave"
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
            results.addView(description("A procurar no catálogo..."))

            CatalogRepository.search(query) { response ->
                results.removeAllViews()

                response.onSuccess { items ->
                    if (items.isEmpty()) {
                        results.addView(card(
                            "Nenhum resultado disponível",
                            "Não encontrámos conteúdos compatíveis nesta fonte. " +
                            "Experimenta outro título."
                        ))
                    } else {
                        results.addView(
                            text("${items.size} RESULTADOS",
                                10f, purple, true).apply {
                                setPadding(0, dp(18), 0, dp(10))
                            }
                        )

                        items.take(30).forEach { item ->
                            val cover = android.widget.ImageView(this).apply {
                                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                                background = rounded(panel, 12)
                                contentDescription = "Capa de ${item.title}"
                            }

                            loadCover(item.identifier, cover)

                            val info = LinearLayout(this).apply {
                                orientation = LinearLayout.VERTICAL

                                addView(text(item.title, 15f, white, true))

                                addView(text(
                                    "${item.year}  •  ${item.durationLabel}",
                                    11f, purple
                                ).apply {
                                    setPadding(0, dp(6), 0, dp(5))
                                })

                                addView(text(
                                    "Autor: ${item.creator}\nLicença declarada: ${item.licenseLabel}",
                                    11f, muted
                                ))

                                addView(text(
                                    "VER DETALHES  →",
                                    10f, purple, true
                                ).apply {
                                    setPadding(0, dp(12), 0, 0)
                                })
                            }

                            val movie = LinearLayout(this).apply {
                                orientation = LinearLayout.HORIZONTAL
                                gravity = Gravity.CENTER_VERTICAL
                                background = rounded(panel, 16)
                                setPadding(dp(12), dp(12), dp(12), dp(12))

                                addView(cover,
                                    LinearLayout.LayoutParams(dp(104), dp(148))
                                )

                                addView(info,
                                    LinearLayout.LayoutParams(0, -2, 1f).apply {
                                        leftMargin = dp(13)
                                    }
                                )

                                setOnClickListener {
                                    buildMovieDetails(item)
                                }
                            }

                            results.addView(movie,
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

        val searchButton = button("PESQUISAR") {
            performSearch(search.text.toString())
        }

        content.addView(searchButton,
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(10)
            }
        )

        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(search.text.toString())
                true
            } else {
                false
            }
        }

        content.addView(results,
            LinearLayout.LayoutParams(-1, -2).apply {
                topMargin = dp(17)
            }
        )

        performSearch("")
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

        content.addView(button("CONSULTAR FONTE ORIGINAL  ↗") {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse(item.sourceUrl)
            ))
        }, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(8)
        })
    }

    private fun buildPlaceholder(title: String, subtitle: String) {
        content.addView(smallLabel("AIZEN TV"))
        content.addView(heading(title))
        content.addView(description(subtitle))

        content.addView(card(
            "Em desenvolvimento",
            "Esta secção será ligada às funcionalidades correspondentes nas próximas etapas."
        ))
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
