package space.work.training.izi.model

import androidx.room.PrimaryKey

data class Img(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var imgId: String = "",
    var img: String = "",
    var text: String = "", var publisher: String = "",
    var views: String = "",
    var timestamp: String = ""
)
