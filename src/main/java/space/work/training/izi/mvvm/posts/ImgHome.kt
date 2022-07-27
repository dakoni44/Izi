package space.work.training.izi.mvvm.posts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import space.work.training.izi.model.Img

@Entity(tableName = "home_imgs")
data class ImgHome(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var uid: String = "",
    var imgs: ArrayList<Img> = ArrayList()
)