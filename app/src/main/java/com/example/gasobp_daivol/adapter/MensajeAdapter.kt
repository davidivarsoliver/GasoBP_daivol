package com.example.gasobp_daivol.adapter;


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.gasobp_daivol.R
import com.example.gasobp_daivol.entities.Mensaje
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*

class MensajeAdapter(context: Context, private val mensajes: MutableList<Mensaje>) :
    ArrayAdapter<Mensaje>(context, 0, mensajes) {

    private val userColors = mutableMapOf<String, Int>()
    private var colorRef: DatabaseReference? = null

    fun setColorRef(ref: DatabaseReference) {
        colorRef = ref
    }

    fun setUserColor(userId: String, color: Int) {
        userColors[userId] = color
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mensaje = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_mensaje, parent, false)

        val usuarioTextView = view.findViewById<TextView>(R.id.usuarioTextView)
        val contenidoTextView = view.findViewById<TextView>(R.id.contenidoTextView)
        val fechaTextView = view.findViewById<TextView>(R.id.fechaTextView)

        usuarioTextView.text = mensaje?.usuarioId
        contenidoTextView.text = mensaje?.contenido
        fechaTextView.text = mensaje?.timestamp?.let { formatTimestamp(it) }

        val userId = mensaje?.usuarioId ?: ""
        val userColor = userColors[userId] ?: Color.WHITE
        contenidoTextView.setBackgroundColor(userColor)

        return view
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

