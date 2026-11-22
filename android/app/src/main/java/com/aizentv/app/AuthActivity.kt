package com.aizentv.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import java.util.concurrent.Executor

class AuthActivity : Activity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var status: TextView

    private val bg = Color.rgb(8, 8, 13)
    private val purple = Color.rgb(168, 85, 247)
    private val white = Color.WHITE
    private val muted = Color.rgb(164, 160, 178)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        buildScreen()

        auth.pendingAuthResult
            ?.addOnSuccessListener { finishLogin() }
            ?.addOnFailureListener {
                showMessage("Não foi possível concluir a autenticação.")
            }
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(25), dp(25), dp(25), dp(25))
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this)
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }

        panel.addView(label("AIZEN", 34f, white, true))
        panel.addView(label("TV", 34f, purple, true))
        panel.addView(label("O teu universo de entretenimento", 14f, muted))
        panel.addView(label("ENTRA OU CRIA A TUA CONTA", 12f, purple, true))

        emailInput = field(
            "Endereço de email",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
        passwordInput = field(
            "Palavra-passe",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        )

        panel.addView(emailInput, params(0, 58))
        panel.addView(passwordInput, params(0, 58))

        panel.addView(actionButton("ENTRAR") { signIn() }, params(0, 54))
        panel.addView(actionButton("CRIAR CONTA") { register() }, params(0, 54))
        panel.addView(actionButton("RECUPERAR PALAVRA-PASSE") {
            resetPassword()
        }, params(0, 48))

        panel.addView(label("OU CONTINUA COM", 11f, muted, true))
        panel.addView(actionButton("CONTINUAR COM GOOGLE") {
            signInGoogle()
        }, params(0, 52))
        panel.addView(actionButton("CONTINUAR COM APPLE") {
            signInApple()
        }, params(0, 52))

        status = label("", 12f, muted)
        panel.addView(status)

        scroll.addView(panel)
        root.addView(scroll)
        setContentView(root)
    }

    private fun label(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER
        if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
        setPadding(dp(5), dp(8), dp(5), dp(8))
    }

    private fun field(hintText: String, type: Int) =
        EditText(this).apply {
            hint = hintText
            inputType = type
            setSingleLine(true)
            textSize = 14f
            setTextColor(white)
            setHintTextColor(muted)
            setPadding(dp(14), 0, dp(14), 0)
            background = GradientDrawable().apply {
                setColor(Color.rgb(22, 20, 31))
                cornerRadius = dp(13).toFloat()
            }
        }

    private fun actionButton(
        value: String,
        action: () -> Unit
    ) = TextView(this).apply {
        text = value
        textSize = 13f
        setTextColor(white)
        gravity = Gravity.CENTER
        background = GradientDrawable().apply {
            setColor(purple)
            cornerRadius = dp(13).toFloat()
        }
        setOnClickListener { action() }
    }

    private fun params(top: Int, height: Int) =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(height)
        ).apply {
            topMargin = dp(top)
            bottomMargin = dp(5)
        }

    private fun dp(value: Int) =
        (value * resources.displayMetrics.density).toInt()

    private fun showMessage(message: String) {
        status.text = message
    }

    private fun email(): String =
        emailInput.text.toString().trim()

    private fun password(): String =
        passwordInput.text.toString()

    private fun validInput(): Boolean {
        if (!email().contains("@")) {
            showMessage("Introduz um endereço de email válido.")
            return false
        }
        if (password().length < 6) {
            showMessage("A palavra-passe deve ter pelo menos 6 caracteres.")
            return false
        }
        return true
    }

    private fun register() {
        if (!validInput()) return

        auth.createUserWithEmailAndPassword(email(), password())
            .addOnSuccessListener {
                auth.currentUser?.sendEmailVerification()
                    ?.addOnSuccessListener {
                        showMessage("Conta criada. Confirma o email antes de entrar.")
                    }
                    ?.addOnFailureListener {
                        showMessage("Conta criada, mas não foi possível enviar a confirmação.")
                    }
            }
            .addOnFailureListener {
                showMessage(it.localizedMessage ?: "Não foi possível criar a conta.")
            }
    }

    private fun signIn() {
        if (!validInput()) return

        auth.signInWithEmailAndPassword(email(), password())
            .addOnSuccessListener { finishLogin() }
            .addOnFailureListener {
                showMessage(it.localizedMessage ?: "Não foi possível entrar.")
            }
    }

    private fun resetPassword() {
        if (!email().contains("@")) {
            showMessage("Introduz primeiro o teu email.")
            return
        }

        auth.sendPasswordResetEmail(email())
            .addOnSuccessListener {
                showMessage("Pedido enviado. Verifica a tua caixa de email.")
            }
            .addOnFailureListener {
                showMessage(it.localizedMessage ?: "Não foi possível enviar o pedido.")
            }
    }

    private fun finishLogin() {
        val user = auth.currentUser

        if (user?.isEmailVerified == true) {
            openCatalog()
        } else {
            user?.sendEmailVerification()
            showMessage("Confirma o teu endereço de email para entrar no AIZEN TV.")
        }
    }

    private fun openCatalog() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun signInGoogle() {
        val clientIdId = resources.getIdentifier(
            "default_web_client_id", "string", packageName
        )

        if (clientIdId == 0) {
            showMessage("O acesso Google ainda precisa de configuração no Firebase.")
            return
        }

        val option = GetGoogleIdOption.Builder()
            .setServerClientId(getString(clientIdId))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val manager = CredentialManager.create(this)

        val callback =
            object : CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                override fun onResult(result: GetCredentialResponse) {
                    val credential = result.credential

                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        try {
                            val token = GoogleIdTokenCredential.createFrom(credential.data)
                            val firebaseCredential =
                                GoogleAuthProvider.getCredential(token.idToken, null)

                            auth.signInWithCredential(firebaseCredential)
                                .addOnSuccessListener { finishLogin() }
                                .addOnFailureListener {
                                    showMessage("Não foi possível autenticar com Google.")
                                }
                        } catch (_: GoogleIdTokenParsingException) {
                            showMessage("Não foi possível ler a conta Google.")
                        }
                    } else {
                        showMessage("Tipo de conta Google não reconhecido.")
                    }
                }

                override fun onError(e: GetCredentialException) {
                    showMessage("A autenticação Google foi cancelada ou falhou.")
                }
            }

        manager.getCredentialAsync(
            this,
            request,
            null,
            Executor { command -> runOnUiThread(command) },
            callback
        )
    }

    private fun signInApple() {
        val provider = OAuthProvider.newBuilder("apple.com")
        provider.setScopes(listOf("email", "name"))

        auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener { finishLogin() }
            .addOnFailureListener {
                showMessage("A autenticação Apple ainda precisa de configuração.")
            }
    }
}
