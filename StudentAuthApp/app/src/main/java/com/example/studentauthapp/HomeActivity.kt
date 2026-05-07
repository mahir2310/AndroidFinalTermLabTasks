package com.example.studentauthapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var rootView: View
    private lateinit var avatarView: TextView
    private lateinit var statusBadge: TextView
    private lateinit var displayNameView: TextView
    private lateinit var emailView: TextView
    private lateinit var uidView: TextView
    private lateinit var createdView: TextView
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var updatePasswordButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rootView = findViewById(R.id.homeRoot)
        avatarView = findViewById(R.id.homeAvatar)
        statusBadge = findViewById(R.id.homeStatusBadge)
        displayNameView = findViewById(R.id.homeDisplayName)
        emailView = findViewById(R.id.homeEmailValue)
        uidView = findViewById(R.id.homeUidValue)
        createdView = findViewById(R.id.homeCreatedValue)
        newPasswordLayout = findViewById(R.id.homeNewPasswordLayout)
        confirmPasswordLayout = findViewById(R.id.homeConfirmPasswordLayout)
        newPasswordInput = findViewById(R.id.homeNewPasswordInput)
        confirmPasswordInput = findViewById(R.id.homeConfirmPasswordInput)
        updatePasswordButton = findViewById(R.id.updatePasswordButton)
        logoutButton = findViewById(R.id.logoutButton)
        deleteButton = findViewById(R.id.deleteAccountButton)
        progressBar = findViewById(R.id.homeProgress)

        logoutButton.setOnClickListener { signOut() }
        updatePasswordButton.setOnClickListener { changePassword() }
        deleteButton.setOnClickListener { confirmDeleteAccount() }

        bindUserInfo()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            goToLogin()
        }
    }

    private fun bindUserInfo() {
        val user = auth.currentUser ?: run {
            goToLogin()
            return
        }

        val email = user.email.orEmpty()
        val displayName = user.displayName?.takeIf { it.isNotBlank() } ?: getString(R.string.default_student_name)
        val uidShort = user.uid.take(8)
        val createdTimestamp = user.metadata?.creationTimestamp
        val createdText = if (createdTimestamp != null && createdTimestamp > 0) {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(createdTimestamp))
        } else {
            getString(R.string.unavailable)
        }

        avatarView.text = email.firstOrNull()?.uppercaseChar()?.toString() ?: "S"
        displayNameView.text = displayName
        emailView.text = email
        uidView.text = uidShort
        createdView.text = createdText
        statusBadge.text = getString(R.string.logged_in_status)
        statusBadge.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    }

    private fun changePassword() {
        newPasswordLayout.error = null
        confirmPasswordLayout.error = null

        val newPassword = newPasswordInput.text?.toString().orEmpty()
        val confirmPassword = confirmPasswordInput.text?.toString().orEmpty()

        when {
            newPassword.length < 8 -> {
                newPasswordLayout.error = getString(R.string.error_password_min)
                newPasswordInput.requestFocus()
                return
            }
            newPassword != confirmPassword -> {
                confirmPasswordLayout.error = getString(R.string.error_passwords_mismatch)
                confirmPasswordInput.requestFocus()
                return
            }
        }

        setLoading(true)
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task: Task<Void> ->
                setLoading(false)
                if (task.isSuccessful) {
                    newPasswordInput.text?.clear()
                    confirmPasswordInput.text?.clear()
                    Snackbar.make(rootView, getString(R.string.success_password_updated), Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(
                        rootView,
                        task.exception?.localizedMessage ?: getString(R.string.error_generic_update_password),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_account)
            .setMessage(R.string.delete_account_message)
            .setPositiveButton(R.string.delete_confirm) { _: DialogInterface, _: Int -> deleteAccount() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteAccount() {
        setLoading(true)
        auth.currentUser?.delete()
            ?.addOnCompleteListener { task: Task<Void> ->
                setLoading(false)
                if (task.isSuccessful) {
                    goToLogin()
                } else {
                    Snackbar.make(
                        rootView,
                        task.exception?.localizedMessage ?: getString(R.string.error_generic_delete_account),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun signOut() {
        auth.signOut()
        goToLogin()
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        updatePasswordButton.isEnabled = !loading
        deleteButton.isEnabled = !loading
        logoutButton.isEnabled = !loading
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}
