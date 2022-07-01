package space.work.training.izi.model

data class Chat(
    var message: String = "",
    var reciever: String = "",
    var sender: String = "",
    var timestamp: String = "",
    var isSeen: Boolean = false
)
