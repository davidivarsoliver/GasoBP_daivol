package com.example.gasobp_daivol.activities;


import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gasobp_daivol.adapter.MensajeAdapter
import com.example.gasobp_daivol.databinding.ActivityMensajeBinding
import com.example.gasobp_daivol.entities.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MensajeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMensajeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var chatRef: DatabaseReference
    private lateinit var colorRef: DatabaseReference
    private lateinit var mensajeAdapter: MensajeAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMensajeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("color_preferences", MODE_PRIVATE)
        database = FirebaseDatabase.getInstance()
        chatRef = database.getReference("chat/mensajes")
        colorRef = database.getReference("user_colors")
        auth = FirebaseAuth.getInstance()

        mensajeAdapter = MensajeAdapter(this, mutableListOf())
        binding.chatListView.adapter = mensajeAdapter

        binding.enviarButton.setOnClickListener {
            val mensaje = binding.mensajeEditText.text.toString().trim()
            if (mensaje.isNotEmpty()) {
                enviarMensaje(mensaje)
                binding.mensajeEditText.text.clear()
            }
        }

        binding.colorButton.setOnClickListener {
            showColorSelectionDialog()
        }

        // Cargar los mensajes existentes y escuchar cambios en tiempo real
        cargarMensajes()
        mensajeAdapter.setColorRef(colorRef)
    }

    private fun cargarMensajes() {
        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val mensaje = snapshot.getValue(Mensaje::class.java)
                mensaje?.let {
                    mensajeAdapter.add(it)
                    it.usuarioId?.let { userId -> fetchUserColor(userId) }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun enviarMensaje(contenido: String) {
        val usuarioId = auth.currentUser?.email ?: ""
        val timestamp = System.currentTimeMillis()

        val mensaje = Mensaje(usuarioId, contenido, timestamp)
        chatRef.push().setValue(mensaje)
    }

    private fun showColorSelectionDialog() {
        val colors = arrayOf("Blanco", "Rojo", "Azul", "Verde", "Amarillo")
        val colorValues = arrayOf(Color.WHITE, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona un color")
        builder.setItems(colors) { _, which ->
            val selectedColor = colorValues[which]
            saveUserColor(selectedColor)
        }
        builder.show()
    }

    private fun saveUserColor(color: Int) {
        val userId = auth.currentUser?.email ?: return
        colorRef.child(userId.replace(".", ",")).setValue(color)
    }

    private fun fetchUserColor(userId: String) {
        colorRef.child(userId.replace(".", ",")).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val color = snapshot.getValue(Int::class.java) ?: Color.WHITE
                mensajeAdapter.setUserColor(userId, color)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}



