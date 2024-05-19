package com.example.gasobp_daivol

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("name2") val name2: String,
    @SerializedName("stock") val stock: Boolean,
    @SerializedName("genero") val genero: String,
    @SerializedName("url") val url: String,
    @SerializedName("precio") val precio: Double
) : Serializable

