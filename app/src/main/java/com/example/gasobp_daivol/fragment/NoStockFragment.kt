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
import com.example.gasobp_daivol.databinding.FragmentNoStockBinding
import com.example.gasobp_daivol.utils.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NoStockFragment : Fragment() {

    private lateinit var binding: FragmentNoStockBinding
    private lateinit var apiService: APIService
    private lateinit var configuracionManager: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoStockBinding.inflate(inflater, container, false)
        configuracionManager = (requireActivity() as MainActivity).configuracionManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = getRetrofit().create(APIService::class.java)

        cargarProductosFueraDeStock()
    }

    private fun cargarProductosFueraDeStock() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getProductosFueraDeStock().execute()
                if (response.isSuccessful) {
                    val productos: List<ProductoResponse>? = response.body()
                    requireActivity().runOnUiThread {
                        showProductos(productos)
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error de consulta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al obtener productos fuera de stock: ${e.message}")
            }
        }
    }


    private fun showProductos(productos: List<ProductoResponse>?) {
        if (productos != null) {
            val adapter = ProductAdapter(productos, configuracionManager.modoNoche) { producto ->
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
            binding.recyclerViewNoStock.adapter = adapter
        } else {
            Log.e("Error", "La lista de productos fuera de stock es nula.")
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ServerConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
