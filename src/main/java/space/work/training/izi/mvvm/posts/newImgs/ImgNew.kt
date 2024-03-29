package space.work.training.izi.mvvm.posts.newImgs

import androidx.room.Entity
import androidx.room.PrimaryKey
import space.work.training.izi.model.Img

@Entity(tableName = "new_imgs")
data class ImgNew(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var uId: String = "",
    var imgs: ArrayList<Img> = ArrayList()
)
