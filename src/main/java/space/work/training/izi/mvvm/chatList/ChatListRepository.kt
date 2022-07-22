package space.work.training.izi.mvvm.chatList

import androidx.lifecycle.LiveData
import space.work.training.izi.model.User
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

    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }

}