package com.app.wildtreasure.ui.pres

import android.app.Application
import androidx.room.Room
import com.app.mylibrary.AppDatabase

class AP : Application() {
    lateinit var dB : AppDatabase

    override fun onCreate() {
        super.onCreate()
        dB = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "DataUrl"
        ).build()



    }
}