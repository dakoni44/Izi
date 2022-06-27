package com.social.world.tracy.mvvm.kotlin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "img_table")
data class Img(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var imgId: String,
    var img: String,
    var text: String, var publisher: String,
    var views: String
)
