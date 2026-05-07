package com.example.studentauthapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var rootView: View
    private lateinit var nameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var registerButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        rootView = findViewById(R.id.registerRoot)
        nameLayout = findViewById(R.id.registerNameLayout)
        emailLayout = findViewById(R.id.registerEmailLayout)
        passwordLayout = findViewById(R.id.registerPasswordLayout)
        confirmPasswordLayout = findViewById(R.id.registerConfirmPasswordLayout)
        nameInput = findViewById(R.id.registerNameInput)
        emailInput = findViewById(R.id.registerEmailInput)
        passwordInput = findViewById(R.id.registerPasswordInput)
        confirmPasswordInput = findViewById(R.id.registerConfirmPasswordInput)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.registerProgress)

        registerButton.setOnClickListener { attemptRegister() }
        findViewById<View>(R.id.loginLinkText).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            goToHome()
        }
    }

    private fun attemptRegister() {
        clearErrors()

        val fullName = nameInput.text?.toString()?.trim().orEmpty()
        val email = emailInput.text?.toString()?.trim().orEmpty()
        val password = passwordInput.text?.toString().orEmpty()
        val confirmPassword = confirmPasswordInput.text?.toString().orEmpty()

        when {
            fullName.isBlank() -> {
                nameLayout.error = getString(R.string.error_full_name_required)
                nameInput.requestFocus()
                return
            }
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
            password.length < 8 -> {
                passwordLayout.error = getString(R.string.error_password_min)
                passwordInput.requestFocus()
                return
            }
            password != confirmPassword -> {
                confirmPasswordLayout.error = getString(R.string.error_passwords_mismatch)
                confirmPasswordInput.requestFocus()
                return
            }
        }

        setLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()

                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask: Task<Void> ->
                            setLoading(false)
                            if (profileTask.isSuccessful) {
                                goToHome()
                            } else {
                                goToHome()
                            }
                        } ?: run {
                        setLoading(false)
                        goToHome()
                    }
                } else {
                    setLoading(false)
                    showError(task.exception?.localizedMessage ?: getString(R.string.error_generic_register))
                }
            }
    }

    private fun clearErrors() {
        nameLayout.error = null
        emailLayout.error = null
        passwordLayout.error = null
        confirmPasswordLayout.error = null
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        registerButton.isEnabled = !loading
    }

    private fun goToHome() {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun showError(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}
