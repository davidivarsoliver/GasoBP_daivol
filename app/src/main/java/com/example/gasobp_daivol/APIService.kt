package com.example.gasobp_daivol

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path


interface APIService {
    @GET("images")
    suspend fun getProducto(): Response<List<ProductoResponse>>

    @GET("/images/genero/{genero}")
    suspend fun getProductoPorGenero(@Path("genero") genero: String): Response<List<ProductoResponse>>

    @PUT("images/{id}/cambiarStock")
    fun pedirStock(@Path("id") productId: Int): Call<Unit>

    @GET("images/nostock")
    fun getProductosFueraDeStock(): Call<List<ProductoResponse>>
}
