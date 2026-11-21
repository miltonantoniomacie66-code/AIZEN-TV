package com.aizentv.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private val bg = Color.rgb(8, 8, 13)
    private val panel = Color.rgb(20, 19, 29)
    private val purple = Color.rgb(168, 85, 247)
    private val white = Color.WHITE
    private val muted = Color.rgb(160, 157, 174)

    private lateinit var content: LinearLayout
    private lateinit var navigation: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        showHome()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun background(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun showHome() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(18), dp(20), dp(8))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = text("AIZEN", 27f, white, true)
        val tv = text(" TV", 27f, purple, true)
        header.addView(brand)
        header.addView(tv)
        root.addView(header)

        root.addView(
            text("O teu próximo universo de entretenimento.",
                13f, muted).apply {
                setPadding(0, dp(5), 0, dp(20))
            }
        )

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                -1, 0, 1f
            )
        }
        root.addView(content)

        navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(8))
        }
        root.addView(navigation)

        setContentView(root)
        showPage("Início")
    }

    private fun showPage(page: String) {
        content.removeAllViews()

        when (page) {
            "Início" -> {
                content.addView(
                    text("ENTRETENIMENTO SEM LIMITES",
                        11f, purple, true)
                )

                val hero = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(20), dp(20), dp(20), dp(20))
                    background = background(panel, 18)
                }

                hero.addView(text("BEM-VINDO AO", 12f, muted, true))
                hero.addView(
                    text("AIZEN TV", 34f, white, true).apply {
                        setPadding(0, dp(8), 0, dp(8))
                    }
                )
                hero.addView(
                    text("Filmes, séries e conteúdos autorizados num só lugar.",
                        14f, muted)
                )
                content.addView(
                    hero,
                    LinearLayout.LayoutParams(-1, dp(210)).apply {
                        topMargin = dp(14)
                        bottomMargin = dp(24)
                    }
                )

                content.addView(
                    text("Descobre o teu próximo favorito",
                        20f, white, true)
                )
                content.addView(
                    text("O catálogo AIZEN TV será apresentado aqui.",
                        14f, muted).apply {
                        setPadding(0, dp(8), 0, 0)
                    }
                )
            }

            "Explorar" -> pageContent(
                "Explorar",
                "Pesquisa e descobre filmes, séries e categorias."
            )

            "Ao Vivo" -> pageContent(
                "Ao Vivo",
                "Canais e transmissões de fontes autorizadas."
            )

            "Downloads" -> pageContent(
                "Downloads",
                "Os teus conteúdos disponíveis para ver offline."
            )

            "Perfil" -> pageContent(
                "O teu perfil",
                "A tua conta, preferências e definições."
            )
        }

        val items = listOf("Início", "Explorar", "Ao Vivo", "Downloads", "Perfil")
        navigation.removeAllViews()

        items.forEach { item ->
            val selected = item == page
            val button = text(
                item,
                10f,
                if (selected) purple else muted,
                selected
            ).apply {
                gravity = Gravity.CENTER
                setPadding(dp(2), dp(12), dp(2), dp(12))
                setOnClickListener { showPage(item) }
            }

            navigation.addView(
                button,
                LinearLayout.LayoutParams(0, -2, 1f)
            )
        }
    }

    private fun pageContent(title: String, description: String) {
        content.addView(
            text(title, 27f, white, true).apply {
                setPadding(0, dp(18), 0, dp(12))
            }
        )
        content.addView(
            text(description, 14f, muted)
        )
    }
}
