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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val bg = Color.rgb(8, 8, 13)
    private val panel = Color.rgb(20, 19, 29)
    private val purple = Color.rgb(168, 85, 247)
    private val white = Color.WHITE
    private val muted = Color.rgb(160, 157, 174)
    private val categories = listOf(
        "Todos", "Ação", "Aventura", "Comédia",
        "Drama", "Ficção científica", "Documentário",
        "Animação", "Romance"
    )

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
    }

    private fun label(value: String): TextView =
        text(value, 12f, purple, true).apply {
            setPadding(0, dp(8), 0, dp(12))
        }

    private fun heading(value: String): TextView =
        text(value, 21f, white, true).apply {
            setPadding(0, dp(20), 0, dp(8))
        }

    private fun description(value: String): TextView =
        text(value, 14f, muted).apply {
            setPadding(0, 0, 0, dp(12))
        }

    private fun card(title: String, subtitle: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = rounded(panel, 16)
            addView(text(title, 16f, white, true))
            addView(text(subtitle, 13f, muted).apply {
                setPadding(0, dp(6), 0, 0)
            })
        }
    }

    private fun showHome() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(14), dp(20), dp(6))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(text("AIZEN", 27f, white, true))
        header.addView(text(" TV", 27f, purple, true))
        root.addView(header)

        root.addView(
            text("O teu próximo universo de entretenimento.",
                13f, muted).apply {
                setPadding(0, dp(4), 0, dp(14))
            }
        )

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(18))
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(4))
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
                "Canais e transmissões de fontes autorizadas."
            )
            "Downloads" -> buildPlaceholder(
                "Downloads",
                "Os teus conteúdos disponíveis para ver offline."
            )
            "Perfil" -> buildPlaceholder(
                "O teu perfil",
                "A tua conta, preferências e definições."
            )
        }

        buildNavigation(page)
    }

    private fun buildHome() {
        content.addView(label("ENTRETENIMENTO SEM LIMITES"))

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(22))
            background = rounded(panel, 20)
        }
        hero.addView(text("BEM-VINDO AO AIZEN", 12f, muted, true))
        hero.addView(
            text("O teu próximo favorito começa aqui.",
                27f, white, true).apply {
                setPadding(0, dp(10), 0, dp(10))
            }
        )
        hero.addView(
            text("Filmes, séries e conteúdos autorizados num só lugar.",
                14f, muted)
        )

        val exploreButton = text("EXPLORAR CATÁLOGO  →", 13f, white, true).apply {
            gravity = Gravity.CENTER
            background = rounded(purple, 12)
            setPadding(dp(16), dp(13), dp(16), dp(13))
            setOnClickListener { showPage("Explorar") }
        }
        hero.addView(exploreButton, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(20)
        })
        content.addView(hero)

        content.addView(heading("Descobre o teu próximo favorito"))
        content.addView(description(
            "O catálogo será apresentado aqui quando as fontes autorizadas forem ligadas."
        ))

        content.addView(card(
            "AIZEN Spotlight",
            "Destaques selecionados serão apresentados nesta área."
        ), LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = dp(12)
        })

        content.addView(heading("Explora por categoria"))
        content.addView(description(
            "Escolhe uma categoria para começar a descobrir."
        ))
        addCategoryList()

        content.addView(heading("A minha lista"))
        content.addView(card(
            "Ainda sem favoritos",
            "Os conteúdos que guardares aparecerão aqui."
        ))
    }

    private fun addCategoryList() {
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val rows = categories.chunked(3)

        rows.forEach { row ->
            val line = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            row.forEach { category ->
                val chip = text(category, 12f,
                    if (category == selectedCategory) white else muted,
                    category == selectedCategory
                ).apply {
                    gravity = Gravity.CENTER
                    background = rounded(
                        if (category == selectedCategory) purple else panel, 18
                    )
                    setPadding(dp(12), dp(10), dp(12), dp(10))
                    setOnClickListener {
                        selectedCategory = category
                        showPage("Explorar")
                    }
                }
                line.addView(chip, LinearLayout.LayoutParams(0, -2, 1f).apply {
                    setMargins(dp(2), dp(3), dp(2), dp(3))
                })
            }
            wrap.addView(line)
        }
        content.addView(wrap)
    }

    private fun buildExplore() {
        content.addView(heading("Explorar filmes"))
        content.addView(description(
            "Descobre filmes com licenças abertas identificadas."
        ))

        val search = EditText(this).apply {
            hint = "Pesquisar filmes..."
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 15f
            setSingleLine(true)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = rounded(panel, 14)
        }

        content.addView(search)

        val searchButton = text(
            "PESQUISAR FILMES", 13f, white, true
        ).apply {
            gravity = Gravity.CENTER
            background = rounded(purple, 12)
            setPadding(dp(16), dp(13), dp(16), dp(13))
        }

        content.addView(searchButton, LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(10)
            bottomMargin = dp(16)
        })

        val results = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        content.addView(results)

        fun performSearch(query: String) {
            results.removeAllViews()
            results.addView(description("A pesquisar no catálogo..."))

            CatalogRepository.search(query) { response ->
                results.removeAllViews()

                response.onSuccess { items ->
                    if (items.isEmpty()) {
                        results.addView(card(
                            "Nenhum filme encontrado",
                            "Não encontrámos títulos com as licenças abertas que aceitamos."
                        ))
                    } else {
                        results.addView(description(
                            "${items.size} filmes encontrados"
                        ))

                        items.forEach { item ->
                            val movie = card(
                                item.title,
                                "${item.year} • ${item.creator}\n" +
                                "Licença: ${item.licenseLabel}\n" +
                                "Ver na fonte original →"
                            )

                            movie.setOnClickListener {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(item.sourceUrl)
                                )
                                startActivity(intent)
                            }

                            results.addView(
                                movie,
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

        searchButton.setOnClickListener {
            performSearch(search.text.toString())
        }

        performSearch("")
    }

    private fun buildPlaceholder(title: String, subtitle: String) {
        content.addView(heading(title))
        content.addView(description(subtitle))
        content.addView(card(
            "Área em preparação",
            "Esta secção será ligada nas próximas etapas do desenvolvimento."
        ))
    }

    private fun buildNavigation(page: String) {
        val items = listOf("Início", "Explorar", "Ao Vivo", "Downloads", "Perfil")
        navigation.removeAllViews()

        items.forEach { item ->
            val selected = item == page
            val button = text(
                item, 10f,
                if (selected) purple else muted,
                selected
            ).apply {
                gravity = Gravity.CENTER
                setPadding(dp(1), dp(12), dp(1), dp(12))
                setOnClickListener { showPage(item) }
            }
            navigation.addView(button, LinearLayout.LayoutParams(0, -2, 1f))
        }
    }
}
