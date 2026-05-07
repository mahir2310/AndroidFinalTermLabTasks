package com.example.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var rootView: View
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var sendButton: MaterialButton
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        rootView = findViewById(R.id.forgotRoot)
        emailLayout = findViewById(R.id.forgotEmailLayout)
        emailInput = findViewById(R.id.forgotEmailInput)
        sendButton = findViewById(R.id.sendResetButton)
        progressBar = findViewById(R.id.forgotProgress)

        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.forgotBackButton)
            .setNavigationOnClickListener { goToLogin() }
        sendButton.setOnClickListener { sendResetEmail() }
    }

    private fun sendResetEmail() {
        emailLayout.error = null
        val email = emailInput.text?.toString()?.trim().orEmpty()

        when {
            email.isBlank() -> {
                emailLayout.error = getString(R.string.error_email_required)
                emailInput.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailLayout.error = getString(R.string.error_email_invalid)
                emailInput.requestFocus()
                return
            }
        }

        setLoading(true)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.success_reset_email_sent), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Snackbar.make(
                        rootView,
                        task.exception?.localizedMessage ?: getString(R.string.error_generic_reset),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        sendButton.isEnabled = !loading
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        )
        finish()
    }
}
