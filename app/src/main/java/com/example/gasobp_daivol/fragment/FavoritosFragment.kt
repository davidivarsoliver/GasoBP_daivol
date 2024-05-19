package com.example.gasobp_daivol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gasobp_daivol.activities.MainActivity
import com.example.gasobp_daivol.adapter.FavoritosAdapter
import com.example.gasobp_daivol.database.FavoritoApplication
import com.example.gasobp_daivol.databinding.FragmentFavoritosBinding
import com.example.gasobp_daivol.entities.Favorito
import com.example.gasobp_daivol.utils.SwipeToDeleteCallback
import com.example.gasobp_daivol.utils.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritosFragment : Fragment() {

    private lateinit var binding: FragmentFavoritosBinding
    private lateinit var favoritosAdapter: FavoritosAdapter
    private lateinit var configuracionManager: UserPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        configuracionManager = (requireActivity() as MainActivity).configuracionManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView y su adaptador
        favoritosAdapter = FavoritosAdapter(configuracionManager.modoNoche,onDeleteClick = { favorito -> onDelete(favorito) })
        binding.recyclerViewFavoritos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritosAdapter
        }

        // Configurar el ItemTouchHelper para el deslizamiento
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(favoritosAdapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewFavoritos)

        // Obtener la lista de favoritos desde la base de datos y mostrarla en el RecyclerView
        CoroutineScope(Dispatchers.IO).launch {
            val favoritos = FavoritoApplication.database.favoritoDao().getAllFavoritos()
            activity?.runOnUiThread {
                favoritosAdapter.favoritos = favoritos
            }
        }
    }

    private fun onDelete(favorito: Favorito) {
        // Lógica para eliminar el favorito de la base de datos
        CoroutineScope(Dispatchers.IO).launch {
            FavoritoApplication.database.favoritoDao().deleteFavorito(favorito)
            val favoritos = FavoritoApplication.database.favoritoDao().getAllFavoritos()
            activity?.runOnUiThread {
                favoritosAdapter.favoritos = favoritos
            }
        }
    }
}
