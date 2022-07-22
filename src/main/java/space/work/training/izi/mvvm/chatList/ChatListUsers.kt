package space.work.training.izi.mvvm.chatList

import androidx.room.Entity
import androidx.room.PrimaryKey
import space.work.training.izi.model.User

@Entity
data class ChatListUsers(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var uId: String = "",
    var users: ArrayList<User> = ArrayList()
)

