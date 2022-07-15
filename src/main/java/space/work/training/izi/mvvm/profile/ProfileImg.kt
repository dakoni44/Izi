package space.work.training.izi.mvvm.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_imgs")
data class ProfileImg(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var imgId: String = "",
    var img: String = "",
    var text: String = "", var publisher: String = "",
    var views: String = "",
    var timestamp: String = ""
)
