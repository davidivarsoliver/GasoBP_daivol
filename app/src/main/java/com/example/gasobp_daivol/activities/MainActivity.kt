package com.example.gasobp_daivol.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import com.example.gasobp_daivol.R
import com.example.gasobp_daivol.databinding.ActivityMainBinding
import com.example.gasobp_daivol.fragment.FavoritosFragment
import com.example.gasobp_daivol.fragment.NoStockFragment
import com.example.gasobp_daivol.fragment.ProductFragment
import com.example.gasobp_daivol.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var configuracionManager: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Preferencias de usuario
        configuracionManager = UserPreferences(this)
        aplicarTema()

        if (configuracionManager.modoNoche) {
            setTheme(R.style.AppTheme_Night)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idiomaActual = configuracionManager.idioma
        configurarIdioma(idiomaActual)

        // Appbar
        setSupportActionBar(binding.toolbar)
        val navigationView = binding.navigationView
        val backgroundRes = if (configuracionManager.modoNoche) {
            R.color.drawerNightColor
        } else {
            R.color.colorPrimary
        }
        navigationView.setBackgroundResource(backgroundRes)

        // Cargar fragmento de productos
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container_view,
                    ProductFragment()
                )
                .commit()
        }

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        actualizarHeaderUsuario()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_cerrar_sesion -> {
                    cerrarSesion()
                }
                R.id.menu_item_configuracion -> {
                    mostrarConfiguraciones()
                    return@setNavigationItemSelectedListener true
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun actualizarHeaderUsuario() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            Log.d("MainActivity", "User email: $email")
            val username = email.substringBefore("@")
            Log.d("MainActivity", "Username: $username")

            val usernameCapitalizado = username.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            Log.d("MainActivity", "Username capitalizado: $usernameCapitalizado")

            val headerView = binding.navigationView.getHeaderView(0)
            val textViewUsername = headerView.findViewById<TextView>(R.id.textViewUsername)
            textViewUsername.text = "Hola, $usernameCapitalizado"
        } else {
            Log.d("MainActivity", "User email is null")
            val headerView = binding.navigationView.getHeaderView(0)
            val textViewUsername = headerView.findViewById<TextView>(R.id.textViewUsername)
            textViewUsername.text = ""
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_favoritos -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, FavoritosFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.menu_item_mapa -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_item_no_stock -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, NoStockFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.menu_item_mensaje -> {
                val intent = Intent(this, MensajeActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        // Redirigir a la pantalla de autenticación
        startActivity(Intent(this, AuthActivity::class.java))
        // Cerrar la actividad actual después de cerrar sesión
        finish()
    }

    private fun mostrarConfiguraciones() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_configuracion, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Configuraciones")

        val switchModoNoche = dialogView.findViewById<SwitchCompat>(R.id.switchModoNoche)
        switchModoNoche.isChecked = configuracionManager.modoNoche
        val alertDialog = dialogBuilder.create()

        switchModoNoche.setOnCheckedChangeListener { _, isChecked ->
            configuracionManager.modoNoche = isChecked
            aplicarTema()
            alertDialog.dismiss()
        }

        val spinnerIdioma = dialogView.findViewById<Spinner>(R.id.spinnerIdioma)
        val idiomas = arrayOf("Español", "Inglés", "Chino")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, idiomas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIdioma.adapter = adapter
        spinnerIdioma.setSelection(idiomas.indexOf(configuracionManager.idioma))
        spinnerIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val nuevoIdioma = idiomas[position]
                configuracionManager.idioma = nuevoIdioma
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar") { _, _ -> }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar") { _, _ -> }
        alertDialog.show()
    }

    private fun configurarIdioma(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun aplicarTema() {
        if (configuracionManager.modoNoche) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
