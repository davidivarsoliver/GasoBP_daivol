package com.example.gasobp_daivol.activities

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gasobp_daivol.adapter.MensajeAdapter
import com.example.gasobp_daivol.databinding.ActivityMensajeBinding
import com.example.gasobp_daivol.entities.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

class MensajeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMensajeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mensajeAdapter: MensajeAdapter
    private lateinit var auth: FirebaseAuth
    private var mensajesListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMensajeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("color_preferences", MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()
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

        cargarMensajes()
        mensajeAdapter.setColorRef(firestore.collection("user_colors"))
    }

    override fun onDestroy() {
        super.onDestroy()
        mensajesListener?.remove()
    }

    private fun cargarMensajes() {
        mensajesListener = firestore.collection("chat").document("mensajes")
            .collection("messages")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("MensajeActivity", "Error al cargar mensajes", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    for (dc in snapshots.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val mensaje = dc.document.toObject(Mensaje::class.java)
                                mensajeAdapter.add(mensaje)
                                mensaje.usuarioId?.let { userId -> fetchUserColor(userId) }
                            }
                            DocumentChange.Type.MODIFIED -> TODO()
                            DocumentChange.Type.REMOVED -> TODO()
                        }
                    }
                    mensajeAdapter.notifyDataSetChanged()
                }
            }
    }



    private fun enviarMensaje(contenido: String) {
        val usuarioId = auth.currentUser?.email ?: ""
        val timestamp = System.currentTimeMillis()

        val mensaje = Mensaje(usuarioId, contenido, timestamp)
        firestore.collection("chat").document("mensajes")
            .collection("messages")
            .add(mensaje)
            .addOnSuccessListener {
                Log.d("MensajeActivity", "Mensaje enviado: $contenido")
            }
            .addOnFailureListener { e ->
                Log.e("MensajeActivity", "Error al enviar mensaje", e)
            }
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
        firestore.collection("user_colors").document(userId.replace(".", ","))
            .set(mapOf("color" to color))
    }

    private fun fetchUserColor(userId: String) {
        firestore.collection("user_colors").document(userId.replace(".", ","))
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val color = document.getLong("color")?.toInt() ?: Color.WHITE
                    mensajeAdapter.setUserColor(userId, color)
                }
            }
    }
}
