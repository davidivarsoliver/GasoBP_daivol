package com.example.gasobp_daivol.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gasobp_daivol.R
import com.example.gasobp_daivol.databinding.ItemFavoritoBinding
import com.example.gasobp_daivol.entities.Favorito
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class FavoritosAdapter(private val isModoNoche: Boolean, private val onDeleteClick: (Favorito) -> Unit) :
    RecyclerView.Adapter<FavoritosAdapter.FavoritoViewHolder>() {

    var favoritos: List<Favorito> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritoViewHolder {
        val binding = ItemFavoritoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritoViewHolder, position: Int) {
        val favorito = favoritos[position]
        holder.bind(favorito)

        // Aplicar el fondo según la configuración del modo noche
        if (isModoNoche) {
            holder.itemView.setBackgroundResource(R.color.backgroundColorNight)
        } else {
            // Fondo normal
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }
    }

    override fun getItemCount(): Int {
        return favoritos.size
    }

    fun deleteItem(position: Int) {
        val favorito = favoritos[position]
        onDeleteClick(favorito)
    }

    class FavoritoViewHolder(private val binding: ItemFavoritoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorito: Favorito) {
            binding.tvNombreFavorito.text = favorito.nombre
            binding.tvPrecioFavorito.text = "Precio: ${favorito.precio}"

            // Cargar imagen con Picasso
            val imageUrl = "http://${favorito.url.replace("localhost", "192.168.1.51")}"
            Picasso.get().load(imageUrl)
                .config(Bitmap.Config.ARGB_8888)
                .resize(150, 150) // Cambiar tamaño de las imágenes según sea necesario
                .into(binding.imageViewProducto, object : Callback {
                    override fun onSuccess() {
                    }

                    override fun onError(e: Exception?) {
                    }
                })
        }
    }
}
