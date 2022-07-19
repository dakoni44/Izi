package space.work.training.izi.mvvm.chatList.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import space.work.training.izi.model.Chat

@Entity(tableName = "chat_user")
data class ChatUser(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "uId")
    var uId: String = "",
    @ColumnInfo(name = "username")
    var username: String = "",
    @ColumnInfo(name = "img")
    var img: String = "",
    @ColumnInfo(name = "chatList")
    var chatList: ArrayList<Chat> = ArrayList()
)
