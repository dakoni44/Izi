package space.work.training.izi.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var message: String = "",
    var reciever: String = "",
    var sender: String = "",
    var timestamp: String = "",
    var isSeen: Boolean = false
)
