package space.work.training.izi.mvvm.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.work.training.izi.model.ChatList
import space.work.training.izi.mvvm.profile.ProfileImg
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatListRepository @Inject constructor(
    private var userDao: UserDao,
    private var chatListFirebase: ChatListFirebase
) {

    fun load(){
        chatListFirebase.loadChatList()
    }

    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }

}