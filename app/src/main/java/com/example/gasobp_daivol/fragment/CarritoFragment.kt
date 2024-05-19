package com.example.gasobp_daivol.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gasobp_daivol.APIService
import com.example.gasobp_daivol.ServerConfig
import com.example.gasobp_daivol.adapter.CarritoAdapter
import com.example.gasobp_daivol.databinding.FragmentCarritoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CarritoFragment : Fragment() {

    private lateinit var binding: FragmentCarritoBinding
    private lateinit var carritoAdapter: CarritoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView y su adaptador
        carritoAdapter = CarritoAdapter(
            onItemClick = { },
            onPedirStockClick = { producto ->
                pedirStock(producto.id)
            }
        )
        binding.recyclerViewCarrito.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carritoAdapter
        }

        // Obtener la lista de todos los productos desde la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = getRetrofit().create(APIService::class.java)

                val todosLosProductos = apiService.getProducto().body()?.toList() ?: emptyList()
                // Filtrar los productos en el carrito (stock = false)
                val productosEnCarrito = todosLosProductos.filter { !it.stock }

                // Verificar si la lista de productos en el carrito no es nula antes de usarla
                productosEnCarrito.let {
                    // Actualizar el RecyclerView con la lista filtrada
                    activity?.runOnUiThread {
                        carritoAdapter.updateItems(productosEnCarrito)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar errores, por ejemplo, mostrar un mensaje al usuario
            }
        }

        // Configurar el botón "Confirmar Pedido"
        binding.btnConfirmarPedido.setOnClickListener {
            confirmarPedido()
        }
    }

    private fun confirmarPedido() {
        // Realizar la llamada al servicio web para cambiar el stock de los productos
        val selectedItems = carritoAdapter.getSelectedItems()

        if (selectedItems.isNotEmpty()) {
            for (cartItem in selectedItems) {
                pedirStock(cartItem.id)
            }

            // Limpiar el carrito después de confirmar el pedido
            carritoAdapter.clearSelectedItems()

            Toast.makeText(requireContext(), "Pedido confirmado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No hay productos seleccionados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pedirStock(id: Int) {
        // Crear la instancia de Retrofit
        val retrofit = getRetrofit()

        // Crear la instancia de la interfaz del servicio
        val apiService = retrofit.create(APIService::class.java)

        // Crear la llamada para pedir stock
        val call: Call<Unit> = apiService.pedirStock(id)

        // Realizar la llamada asíncrona
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    // La llamada fue exitosa, puedes realizar acciones adicionales aquí si es necesario
                } else {
                    // La llamada no fue exitosa, maneja el error según tus necesidades
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                // Manejar errores de red u otros errores en la llamada
            }
        })
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ServerConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
