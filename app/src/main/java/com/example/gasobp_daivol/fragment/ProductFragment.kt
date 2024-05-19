package com.example.gasobp_daivol.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gasobp_daivol.APIService
import com.example.gasobp_daivol.ProductoResponse
import com.example.gasobp_daivol.R
import com.example.gasobp_daivol.ServerConfig
import com.example.gasobp_daivol.activities.MainActivity
import com.example.gasobp_daivol.adapter.ProductAdapter
import com.example.gasobp_daivol.databinding.FragmentProductBinding
import com.example.gasobp_daivol.utils.UserPreferences
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private lateinit var apiService: APIService
    private lateinit var configuracionManager: UserPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        configuracionManager = (requireActivity() as MainActivity).configuracionManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = getRetrofit().create(APIService::class.java)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_comida -> {
                    cargarProductos("Comida")
                    actualizarTitulo("Productos de Comida")
                }
                R.id.menu_bebida -> {
                    cargarProductos("Bebidas")
                    actualizarTitulo("Productos de Bebida")
                }

            }
            true
        }

        // Llamada inicial al cargar el fragmento
        cargarProductos("Comida")
    }



    private fun cargarProductos(genero: String) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val call = apiService.getProductoPorGenero(genero)

                val productos: List<ProductoResponse>? = call.body()
                Log.d("Productos", "Productos cargados con éxito: $productos")

                requireActivity().runOnUiThread {
                    if (call.isSuccessful) {
                        showProductos(productos)
                    } else {
                        // Mostramos el error
                        Toast.makeText(requireContext(), "Error de consulta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al obtener productos: ${e.message}")
            }
        }
    }


    private fun showProductos(productos: List<ProductoResponse>?) {
        if (productos != null) {
            val adapter = ProductAdapter(productos, configuracionManager.modoNoche) { producto ->
                // Al hacer clic en un producto, abrir el fragmento de detalles del producto
                val detallesFragment = DetailProductFragment()
                val bundle = Bundle().apply {
                    putSerializable("producto", producto)
                }
                detallesFragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container_view, detallesFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
            binding.recyclerViewProductos.adapter = adapter
        } else {
            // Manejar el caso cuando productos es nulo
            Log.e("Error", "La lista de productos es nula.")
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ServerConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Cambia el texto del titulo cuando pasas de genero de producto
    private fun actualizarTitulo(titulo: String) {
        binding.tvTitulo.text = titulo
    }
}