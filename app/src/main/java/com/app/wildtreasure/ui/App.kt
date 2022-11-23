package com.app.wildtreasure.ui

import android.app.Application
import androidx.room.Room
import com.app.mylibrary.AppDatabase

class App : Application() {
    lateinit var dataBase : AppDatabase

    override fun onCreate() {
        super.onCreate()
        dataBase = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "DataUrl"
        ).build()



    }
}