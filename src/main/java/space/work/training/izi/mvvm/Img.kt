package com.social.world.tracy.mvvm.kotlin

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Img(
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    var imgId: String = "",
    var img: String = "",
    var text: String = "", var publisher: String = "",
    var views: String = ""
)
