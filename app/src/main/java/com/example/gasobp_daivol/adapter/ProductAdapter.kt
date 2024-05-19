package com.example.gasobp_daivol.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gasobp_daivol.ProductoResponse
import com.example.gasobp_daivol.R
import com.example.gasobp_daivol.databinding.ItemProductoBinding
import com.example.gasobp_daivol.entities.Favorito
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ProductAdapter(private val productos: List<ProductoResponse>,
                     private val isModoNoche: Boolean,
                     private val onItemClick: (ProductoResponse) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductoBinding.inflate(inflater, parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)

        // Aplicar el fondo según la configuración del modo noche
        if (isModoNoche) {
            holder.itemView.setBackgroundResource(R.color.backgroundColorNight)
        } else {
            // Fondo normal (puede ser transparente o del color que desees)
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }

        //al dar click al itemn, veremos los detalles
        holder.itemView.setOnClickListener{
            onItemClick(producto)
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }


    class ProductoViewHolder(private val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: ProductoResponse) {
            binding.tvNombre.text = producto.name2
            Log.d("Picasso", "Cargando imagen desde: ${producto.url}")

            val imageUrl = "http://${producto.url.replace("localhost", "192.168.1.51")}"
            Picasso.get().load(imageUrl)
                .config(Bitmap.Config.ARGB_8888)
                .resize(150,150) // Cambiar tamaño de las imagenes
                .into(binding.imageViewProducto, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Log.e("Picasso", "Error cargando imagen", e)
                    }
                })


        }
    }
}


