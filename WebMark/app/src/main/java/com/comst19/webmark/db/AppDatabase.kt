package com.comst19.webmark.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(WebMarkEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getWebMarkDao() : WebMarkDao

    companion object{
        val databaseName = "db_webmark"
        var appDatabase : AppDatabase? = null

        fun getInstance(context : Context) : AppDatabase?{
            if(appDatabase == null){
                appDatabase = Room.databaseBuilder(context,
                    AppDatabase::class.java,
                    databaseName).
                fallbackToDestructiveMigration()
                    .build()

            }
            return appDatabase
        }
    }
}