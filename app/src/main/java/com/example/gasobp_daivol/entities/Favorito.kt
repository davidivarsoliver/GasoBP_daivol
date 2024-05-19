package com.example.gasobp_daivol.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class Favorito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val precio: Double,
    val stock: Boolean,
    val genero: String,
    val url: String

)
