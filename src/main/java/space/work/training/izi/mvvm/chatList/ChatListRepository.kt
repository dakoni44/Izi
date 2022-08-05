package space.work.training.izi.mvvm.chatList

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatListRepository @Inject constructor(
    private var userDao: UserDao,
    private var chatListFirebase: ChatListFirebase
) {

    fun load() {
        chatListFirebase.loadChatList()
    }

    fun getAllUsers(id: String): LiveData<ChatListUsers> {
        return userDao.getAllUsers(id)
    }

}