package space.work.training.izi.mvvm.posts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_imgs")
data class Img(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var imgId: String = "",
    var img: String = "",
    var text: String = "", var publisher: String = "",
    var views: String = "",
    var timestamp: String = ""
)
