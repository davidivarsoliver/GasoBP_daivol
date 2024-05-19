package com.example.gasobp_daivol.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gasobp_daivol.entities.Favorito

@Dao
interface FavoritoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorito(favorito: Favorito)

    @Query("SELECT * FROM favoritos")
    fun getAllFavoritos(): List<Favorito>

    @Delete
    suspend fun deleteFavorito(favorito: Favorito)
}

