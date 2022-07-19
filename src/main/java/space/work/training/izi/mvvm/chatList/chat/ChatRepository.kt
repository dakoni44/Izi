package space.work.training.izi.mvvm.chatList.chat

import androidx.lifecycle.LiveData
import space.work.training.izi.model.Chat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private var chatDao: ChatDao,
    private var chatFirebase: ChatFirebase
) {

    fun load() {
        chatFirebase.showData()
    }

    fun getUserChat(id: String): LiveData<ChatUser> {
        return chatDao.getChatUser(id)
    }

    fun setReceiverId(id: String) {
        chatFirebase.setReceiverId(id)
    }

    fun sendMessage(message: String) {
        chatFirebase.sendMessage(message)
    }

}