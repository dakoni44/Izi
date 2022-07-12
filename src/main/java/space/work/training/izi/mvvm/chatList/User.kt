package space.work.training.izi.mvvm.chatList

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var uid: String = "",
    var email: String = "",
    var name: String = "", var username: String = "",
    var image: String = "",var bio: String = ""
)
