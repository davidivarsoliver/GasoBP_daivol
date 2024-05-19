package com.example.gasobp_daivol.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gasobp_daivol.APIService
import com.example.gasobp_daivol.ProductoResponse
import com.example.gasobp_daivol.R
import com.example.gasobp_daivol.ServerConfig
import com.example.gasobp_daivol.database.FavoritoApplication
import com.example.gasobp_daivol.databinding.FragmentDetailsProductBinding
import com.example.gasobp_daivol.entities.Favorito
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailProductFragment : Fragment() {

    private lateinit var binding: FragmentDetailsProductBinding
    private lateinit var btnAgregarAlCarrito: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener los argumentos del producto
        val producto = arguments?.getSerializable("producto") as? ProductoResponse

        // Mostrar la información del producto
        if (producto != null) {
            mostrarDetalles(producto)

            // Configurar el botón "Agregar al Carrito"
            btnAgregarAlCarrito = binding.btnAgregarAlCarrito
            btnAgregarAlCarrito.setOnClickListener {
                agregarAlCarrito(producto?.id)
            }
            configurarInterfazUsuario(producto)
        }
    }


    private fun mostrarDetalles(producto: ProductoResponse) {
        // UI con información del producto
        binding.tvNombre.text = producto.name2
        binding.tvPrecio.text = "Precio: ${producto.precio}"

        val imageUrl = "http://${producto.url.replace("localhost", "192.168.1.51")}"
        Picasso.get().load(imageUrl)
            .config(Bitmap.Config.ARGB_8888)
            .resize(150,150) // Cambiar tamaño de las imagenes
            .into(binding.imageViewProducto, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    Log.e("Picasso", "Error cargando imagen", e)
                }
            })

        val estadoStockText = if (producto.stock) "En stock" else "Sin stock"
        val estadoStockColor = if (producto.stock) R.color.colorStockEnStock else R.color.colorStockSinStock

        binding.tvEstadoStock.text = estadoStockText
        binding.tvEstadoStock.setTextColor(ContextCompat.getColor(requireContext(), estadoStockColor))

        setupFavoritoButton(producto)
    }

    private fun setupFavoritoButton(producto: ProductoResponse) {
        binding.btnFavorito.setOnClickListener {
            // Crear el objeto Favorito y guardarlo en la base de datos
            val favorito = Favorito(nombre = producto.name2, precio = producto.precio,
                stock = producto.stock, genero = producto.genero, url = producto.url ?: "")
            CoroutineScope(Dispatchers.IO).launch {
                FavoritoApplication.database.favoritoDao().insertFavorito(favorito)
            }

            Toast.makeText(requireContext(), "Producto añadido a favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ServerConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun agregarAlCarrito(idProducto: Int?) {
        idProducto?.let {
            pedirStock(it)
        }
    }

    private fun pedirStock(id: Int) {
        // Obtener la instancia de ApiService
        val retrofit = getRetrofit()
        val apiService = retrofit.create(APIService::class.java)

        // Realizar la solicitud de pedido de stock
        val call: Call<Unit> = apiService.pedirStock(id)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.d("PedidoStock", "Respuesta: $response")
                if (response.isSuccessful) {
                    mostrarMensaje("Producto pedido a la compañía. En breves le llegará el stock.")
                    deshabilitarBotonPedirStock()
                } else {
                    mostrarMensaje("Error al pedir stock. Inténtalo de nuevo. Código de respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("PedidoStock", "Error al pedir stock: ${t.message}")
                mostrarMensaje("Error al pedir stock. Verifica tu conexión a internet. Error: ${t.message}")
            }
        })
    }



    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun deshabilitarBotonPedirStock() {
        btnAgregarAlCarrito.isEnabled = false
    }
    private fun configurarInterfazUsuario(producto: ProductoResponse) {
        btnAgregarAlCarrito.visibility = if (producto.stock) View.GONE else View.VISIBLE
    }
}


