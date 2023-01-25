package com.comst19.webmark.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WebMarkDao {

    @Query("SELECT * FROM WebMarkEntity ORDER BY id DESC")
    fun getAllWebMark () :List<WebMarkEntity>

    @Insert
    fun insertWebMark(webmark : WebMarkEntity)

    @Update
    fun updateWebMark(webmark : WebMarkEntity)

    @Delete
    fun deleteWebMark(webmark : WebMarkEntity)
}
