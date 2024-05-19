package com.example.gasobp_daivol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gasobp_daivol.ProductoResponse
import com.example.gasobp_daivol.databinding.ItemCarritoBinding

class CarritoAdapter(
    private val onItemClick: (ProductoResponse) -> Unit,
    private val onPedirStockClick: (ProductoResponse) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    var items: List<ProductoResponse> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding =
            ItemCarritoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<ProductoResponse>) {
        this.items = items
        notifyDataSetChanged()
    }

    private val selectedItems = mutableListOf<ProductoResponse>()


    // Función para obtener los productos seleccionados
    fun getSelectedItems(): List<ProductoResponse> {
        return selectedItems.toList()
    }

    // Función para limpiar la selección de productos
    fun clearSelectedItems() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    inner class CarritoViewHolder(private val binding: ItemCarritoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[position])
                }
            }
        }

        fun bind(producto: ProductoResponse) {
            binding.tvNombreCarrito.text = producto.name2
            binding.tvPrecioCarrito.text = producto.precio.toString()

        }


    }
}

