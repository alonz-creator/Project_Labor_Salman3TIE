package com.example.salman3tie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.salman3tie.data.api.SupabaseApiClient
import com.example.salman3tie.data.model.UserModel
import com.example.salman3tie.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var isRegisterMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.textToggleAuth.setOnClickListener {
            toggleAuthMode()
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.inputUsername.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()
            val name = binding.inputName.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRegisterMode) {
                if (name.isEmpty()) {
                    Toast.makeText(this, "Nama Lengkap tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                performRegister(name, username, password)
            } else {
                performLogin(username, password)
            }
        }
    }

    private fun toggleAuthMode() {
        isRegisterMode = !isRegisterMode
        if (isRegisterMode) {
            binding.inputName.visibility = View.VISIBLE
            binding.btnLogin.text = "Register"
            binding.textToggleAuth.text = "Sudah punya akun? Login di sini"
            binding.textTitleAuth.text = "Daftar Akun Baru"
        } else {
            binding.inputName.visibility = View.GONE
            binding.btnLogin.text = "Login"
            binding.textToggleAuth.text = "Belum punya akun? Register di sini"
            binding.textTitleAuth.text = getString(R.string.title_auth)
        }
    }

    private fun performRegister(name: String, username: String, password: String) {
        lifecycleScope.launch {
            try {
                val newUser = UserModel(
                    name = name,
                    username = username,
                    password = password
                )
                val response = SupabaseApiClient.userApiService.registerUser(newUser)

                if (response.isSuccessful) {
                    MaterialAlertDialogBuilder(this@AuthActivity)
                        .setTitle("Register Berhasil")
                        .setMessage("Akun $username telah terdaftar di Supabase! Silakan Login.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            toggleAuthMode()
                        }
                        .show()
                } else {
                    val errorStr = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@AuthActivity, "Gagal Register (${response.code()}): $errorStr", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("REGISTER_ERROR", e.message ?: "Unknown error")
                Toast.makeText(this@AuthActivity, "Error Register: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performLogin(username: String, password: String) {
        lifecycleScope.launch {
            try {
                // Coba Login ke Supabase
                val users = SupabaseApiClient.userApiService.loginUser("eq.$username", "eq.$password")

                if (users.isNotEmpty()) {
                    val loggedInUser = users[0]
                    saveSessionAndNavigate(loggedInUser.name.ifEmpty { username })
                } else {
                    // Fallback ke pengecekan lokal jika tidak ditemukan di Supabase / kredensial default
                    if ((username == "salman" && password == "12345678") || (username.isNotEmpty() && username == password)) {
                        saveSessionAndNavigate(username)
                    } else {
                        showLoginFailedDialog()
                    }
                }
            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", e.message ?: "Unknown error")
                // Fallback jika belum diisi Supabase URL atau Offline
                if ((username == "salman" && password == "12345678") || (username.isNotEmpty() && username == password)) {
                    saveSessionAndNavigate(username)
                } else {
                    showLoginFailedDialog()
                }
            }
        }
    }

    private fun saveSessionAndNavigate(displayName: String) {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        sharedPref.edit {
            putBoolean("isLogin", true)
            putString("username", displayName)
        }

        val intent = Intent(this@AuthActivity, BaseActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginFailedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Gagal Login")
            .setMessage("Username atau Password salah!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
