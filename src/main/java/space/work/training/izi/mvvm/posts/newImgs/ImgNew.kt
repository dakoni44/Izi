package space.work.training.izi.mvvm.posts.newImgs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "new_imgs")
data class ImgNew(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var imgId: String = "",
    var img: String = "",
    var text: String = "", var publisher: String = "",
    var views: String = "",
    var timestamp: String = ""
)
