package com.comst19.webmark.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WebMarkEntity (

    @PrimaryKey(autoGenerate = true) var id : Int? = null,
    @ColumnInfo(name="webUrl") var url : String,
    @ColumnInfo(name="urlNickname") var nickname : String

    )