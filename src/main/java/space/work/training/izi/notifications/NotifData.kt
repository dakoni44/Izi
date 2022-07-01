package space.work.training.izi.notifications

data class NotifData(
    var sender: String = "",
    var body: String = "",
    var title: String = "",
    var reciever: String = "",
    var icon: Int = 0
)
