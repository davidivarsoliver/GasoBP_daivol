package com.example.gasobp_daivol.database

import android.app.Application
import androidx.room.Room

class FavoritoApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(this, AppDatabase::class.java,
            "app_database").build()
    }
}
